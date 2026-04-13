pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
        allure 'allure'
    }

    // =========================================================================
    // PARAMETERS
    // HOW IT WORKS:
    //   - CHROME_THREADS=3  → spins up 3 parallel Chrome  branches: chrome-1, chrome-2, chrome-3
    //   - FIREFOX_THREADS=2 → spins up 2 parallel Firefox branches: firefox-1, firefox-2
    //   - Set either to 0   → that browser is completely skipped
    //   - Both > 0          → all branches run in parallel simultaneously
    //
    // Example: CHROME_THREADS=3 + FIREFOX_THREADS=2 = 5 total parallel branches
    // =========================================================================
    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['test', 'stage', 'prod'],
            description: 'Target Environment'
        )
        string(
            name: 'TEST_TAGS',
            defaultValue: '@integration',
            description: 'Cucumber tags to execute (e.g. @smoke, @regression)'
        )
        string(
            name: 'CHROME_THREADS',
            defaultValue: '1',
            description: 'Number of parallel Chrome threads (0 = skip Chrome)'
        )
        string(
            name: 'FIREFOX_THREADS',
            defaultValue: '0',
            description: 'Number of parallel Firefox threads (0 = skip Firefox)'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run browsers in headless mode'
        )
        booleanParam(
            name: 'SKIP_TESTS',
            defaultValue: false,
            description: 'Skip test execution (runs only Compile + SonarQube)'
        )
    }

    stages {

        // =====================================================================
        // STAGE 1: CHECKOUT
        // =====================================================================
        stage('Checkout') {
            steps {
                checkout scm
                echo "✅ Code checked out from: ${env.GIT_URL} | Branch: ${env.GIT_BRANCH}"
            }
        }

        // =====================================================================
        // STAGE 2: COMPILE
        // Fail fast — catch compilation errors before wasting any time or
        // infrastructure on Sonar analysis or test execution.
        // =====================================================================
        stage('Compile') {
            steps {
                sh 'mvn clean compile -q'
                echo "✅ Compilation successful"
            }
        }

        // =====================================================================
        // STAGE 3: SONARQUBE ANALYSIS + QUALITY GATE
        // Industry standard: run static analysis BEFORE tests.
        // If code has critical bugs/vulnerabilities, fail fast here — don't
        // waste time spinning up Selenium Grid for broken code.
        // Quality Gate blocks the pipeline until SonarQube finishes processing.
        // =====================================================================
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarServer') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=hybrid-automation-framework'
                }
                echo "✅ SonarQube analysis submitted"
            }
        }

        stage('Quality Gate') {
            steps {
                script {
                    // Wait up to 3 minutes for SonarQube to process the report.
                    // If the Quality Gate fails → pipeline aborts here.
                    // Tests will NOT run on code that fails quality standards.
                    timeout(time: 3, unit: 'MINUTES') {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "❌ Quality Gate FAILED: ${qg.status}. Fix code issues before running tests."
                        }
                    }
                }
                echo "✅ Quality Gate passed"
            }
        }

        // =====================================================================
        // STAGE 4: PARALLEL TEST EXECUTION
        // HOW THE PARALLEL LOGIC WORKS:
        //   We build a map called parallelBranches where each key is a unique
        //   branch name and each value is a closure (the work to do).
        //
        //   CHROME_THREADS=3 adds keys: "CHROME-THREAD-1", "CHROME-THREAD-2", "CHROME-THREAD-3"
        //   FIREFOX_THREADS=2 adds keys: "FIREFOX-THREAD-1", "FIREFOX-THREAD-2"
        //
        //   Groovy's parallel() then executes ALL keys simultaneously.
        //   Each branch runs a full mvn verify independently on the Selenium Grid.
        //
        //   WHY unique keys matter: if two branches had the same key, one would
        //   silently overwrite the other in the map — so we suffix with thread index.
        // =====================================================================
        stage('Test Execution') {
            when {
                expression { !params.SKIP_TESTS }
            }
            steps {
                script {
                    def parallelBranches = [:]

                    // --- Helper closure to build a test branch ---
                    // Extracted as a variable so the browser/index values are
                    // captured correctly in each closure (avoids Groovy loop closure trap).
                    def buildBranch = { String browser, int index ->
                        return {
                            stage("${browser.toUpperCase()}-THREAD-${index}") {
                                script {
                                    def mvnHome = tool name: 'maven3', type: 'maven'
                                    def javaHome = tool name: 'jdk17', type: 'jdk'

                                    withEnv([
                                        "PATH+MAVEN=${mvnHome}/bin",
                                        "JAVA_HOME=${javaHome}"
                                    ]) {
                                        echo "🚀 Starting ${browser.toUpperCase()} thread ${index}"
                                        sh """
                                            mvn verify \
                                                -DrunMode=remote \
                                                -Dgrid.host=selenium-hub \
                                                -Dbrowser=${browser} \
                                                -Dheadless=${params.HEADLESS} \
                                                -Denvironment=${params.ENVIRONMENT} \
                                                -Dcucumber.filter.tags='${params.TEST_TAGS}'
                                        """
                                    }
                                }
                            }
                        }
                    }

                    // --- Build Chrome branches ---
                    // e.g. CHROME_THREADS=3 → adds CHROME-THREAD-1, CHROME-THREAD-2, CHROME-THREAD-3
                    def chromeCount = params.CHROME_THREADS.toInteger()
                    if (chromeCount > 0) {
                        for (int i = 1; i <= chromeCount; i++) {
                            parallelBranches["CHROME-THREAD-${i}"] = buildBranch('chrome', i)
                        }
                        echo "🌐 Chrome: ${chromeCount} parallel thread(s) queued"
                    } else {
                        echo "⏭️  Chrome: skipped (CHROME_THREADS=0)"
                    }

                    // --- Build Firefox branches ---
                    // e.g. FIREFOX_THREADS=2 → adds FIREFOX-THREAD-1, FIREFOX-THREAD-2
                    def firefoxCount = params.FIREFOX_THREADS.toInteger()
                    if (firefoxCount > 0) {
                        for (int i = 1; i <= firefoxCount; i++) {
                            parallelBranches["FIREFOX-THREAD-${i}"] = buildBranch('firefox', i)
                        }
                        echo "🦊 Firefox: ${firefoxCount} parallel thread(s) queued"
                    } else {
                        echo "⏭️  Firefox: skipped (FIREFOX_THREADS=0)"
                    }

                    // --- Guard: must have at least one branch ---
                    if (parallelBranches.isEmpty()) {
                        error "❌ No browsers selected. Set CHROME_THREADS or FIREFOX_THREADS > 0."
                    }

                    echo "⚡ Launching ${parallelBranches.size()} total parallel branch(es)..."
                    parallel parallelBranches
                }
            }
        }
    }

    // =========================================================================
    // POST ACTIONS
    // 'always' runs regardless of pass/fail — ensures reports are never lost.
    // =========================================================================
    post {
        always {
            junit testResults: 'target/surefire-reports/*.xml',
                  allowEmptyResults: true,
                  skipPublishingChecks: true

            archiveArtifacts artifacts: '''
                target/surefire-reports/**,
                target/allure-results/**,
                target/reports/**,
                logs/**
            ''', allowEmptyArchive: true

            allure([
                includeProperties: false,
                jdk: '',
                results: [[path: 'target/allure-results']],
                reportBuildPolicy: 'ALWAYS',
                properties: []
            ])
        }

        success {
            echo "✅ Pipeline PASSED — All ${params.CHROME_THREADS} Chrome + ${params.FIREFOX_THREADS} Firefox thread(s) completed successfully."
        }

        unstable {
            echo "⚠️  Pipeline UNSTABLE — Some tests failed. Check Allure report for details."
        }

        failure {
            echo "❌ Pipeline FAILED — Check console output, SonarQube Quality Gate, or Allure report."
        }
    }
}