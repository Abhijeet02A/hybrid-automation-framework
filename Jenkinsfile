pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
        allure 'allure'
    }

    // =========================================================================
    // PARAMETERS
    //
    // CHROME_THREADS=3  → 3 parallel Chrome  branches: CHROME-THREAD-1,2,3
    // FIREFOX_THREADS=2 → 2 parallel Firefox branches: FIREFOX-THREAD-1,2
    // Set either to 0   → that browser is completely skipped
    // Both > 0          → all branches run simultaneously on the Selenium Grid
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
            description: 'Skip test execution (runs Sonar analysis only)'
        )
    }

    stages {

        // =====================================================================
        // STAGE 1: CHECKOUT
        // =====================================================================
        stage('Checkout') {
            steps {
                checkout scm
                echo "✅ Checked out branch: ${env.GIT_BRANCH}"
            }
        }

        // =====================================================================
        // STAGE 2: SONARQUBE ANALYSIS
        // Runs BEFORE tests — fail fast on code quality issues.
        // -DskipTests means we compile + analyse only, no test execution here.
        // =====================================================================
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarServer') {
                    sh '''
                        mvn clean verify sonar:sonar \
                            -DskipTests \
                            -Dsonar.projectKey=hybrid-automation-framework
                    '''
                }
                echo "✅ SonarQube analysis submitted"
            }
        }

        // =====================================================================
        // STAGE 3: QUALITY GATE
        // Waits for SonarQube to process and checks result via webhook.
        // REQUIRES webhook in SonarQube → http://jenkins:8080/sonarqube-webhook/
        // If Quality Gate fails → pipeline stops here. Tests will NOT run.
        // =====================================================================
        stage('Quality Gate') {
            steps {
                timeout(time: 3, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
                echo "✅ Quality Gate passed"
            }
        }

        // =====================================================================
        // STAGE 4: PARALLEL TEST EXECUTION
        //
        // HOW IT WORKS:
        //   We build a map (parallelBranches) where:
        //     key   = unique branch name e.g. "CHROME-THREAD-1"
        //     value = closure (the mvn verify command for that thread)
        //
        //   CHROME_THREADS=3  adds: CHROME-THREAD-1, CHROME-THREAD-2, CHROME-THREAD-3
        //   FIREFOX_THREADS=2 adds: FIREFOX-THREAD-1, FIREFOX-THREAD-2
        //   parallel() then runs ALL of them simultaneously.
        //
        //   -Pci-run activates the ci-run profile in pom.xml which triggers
        //   allure:report after verify → generates target/allure-results
        //
        //   WHY buildBranch is a separate closure (not inline in the loop):
        //   Groovy closures in for-loops capture variables by REFERENCE.
        //   Without this pattern all threads would use the last loop value.
        // =====================================================================
        stage('Test Execution') {
            when {
                expression { !params.SKIP_TESTS }
            }
            steps {
                script {
                    def parallelBranches = [:]

                    def buildBranch = { String browser, int index ->
                        return {
                            stage("${browser.toUpperCase()}-THREAD-${index}") {
                                sh """
                                    mvn verify \
                                        -Pci-run \
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

                    // --- Build Chrome branches ---
                    def chromeCount = params.CHROME_THREADS.toInteger()
                    if (chromeCount > 0) {
                        for (int i = 1; i <= chromeCount; i++) {
                            parallelBranches["CHROME-THREAD-${i}"] = buildBranch('chrome', i)
                        }
                        echo "🌐 Chrome: ${chromeCount} parallel thread(s) queued"
                    } else {
                        echo "⏭️  Chrome skipped (CHROME_THREADS=0)"
                    }

                    // --- Build Firefox branches ---
                    def firefoxCount = params.FIREFOX_THREADS.toInteger()
                    if (firefoxCount > 0) {
                        for (int i = 1; i <= firefoxCount; i++) {
                            parallelBranches["FIREFOX-THREAD-${i}"] = buildBranch('firefox', i)
                        }
                        echo "🦊 Firefox: ${firefoxCount} parallel thread(s) queued"
                    } else {
                        echo "⏭️  Firefox skipped (FIREFOX_THREADS=0)"
                    }

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
    // POST ACTIONS — always runs regardless of pass/fail
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
                jdk              : '',
                results          : [[path: 'target/allure-results']],
                reportBuildPolicy: 'ALWAYS',
                properties       : []
            ])
        }

        success {
            echo "✅ Pipeline PASSED — Chrome×${params.CHROME_THREADS} Firefox×${params.FIREFOX_THREADS} completed."
        }
        unstable {
            echo "⚠️  Pipeline UNSTABLE — Some tests failed. Check Allure report."
        }
        failure {
            echo "❌ Pipeline FAILED — Check console, Quality Gate, or Allure report."
        }
    }
}