pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
        allure 'allure'          // Make sure you configured this in Global Tools
    }

    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['test', 'stage', 'prod'],
            description: 'Target Environment'
        )
        string(
            name: 'TEST_TAGS',
            defaultValue: '@integration',
            description: 'Cucumber tags to execute (e.g. @smoke,@regression)'
        )
        choice(
            name: 'BROWSERS',
            choices: ['chrome', 'firefox', 'chrome,firefox'],
            description: 'Browser(s) to run (comma separated for parallel)'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run browsers in headless mode'
        )
        booleanParam(
            name: 'SKIP_TESTS',
            defaultValue: false,
            description: 'Skip test execution (for Sonar/build only)'
        )
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test Execution') {
            when {
                expression { !params.SKIP_TESTS }
            }
            steps {
                script {
                    def browsers = params.BROWSERS.split(',')
                    def parallelStages = [:]

                    browsers.each { browser ->
                        parallelStages["${browser.toUpperCase()}"] = {
                            stage("${browser.toUpperCase()}") {
                                script {
                                    def mvnHome = tool name: 'maven3', type: 'maven'
                                    def javaHome = tool name: 'jdk17', type: 'jdk'

                                    withEnv(["PATH+MAVEN=${mvnHome}/bin", "JAVA_HOME=${javaHome}"]) {
                                        sh """
                                            mvn clean verify \
                                                -DrunMode=remote \
                                                -Dgrid.host=selenium-hub \
                                                -Dbrowser=${browser.trim()} \
                                                -Dheadless=${params.HEADLESS} \
                                                -Denvironment=${params.ENVIRONMENT} \
                                                -Dcucumber.filter.tags='${params.TEST_TAGS}'
                                        """
                                    }
                                }
                            }
                        }
                    }

                    parallel parallelStages
                }
            }
        }

        stage('SonarQube Analysis') {
            when {
                expression { !params.SKIP_TESTS }
            }
            steps {
                withSonarQubeEnv('SonarServer') {  // Change name as per your Jenkins config
                    sh 'mvn sonar:sonar -Dsonar.projectKey=hybrid-automation-framework'
                }
            }
        }
    }

    post {
        always {
            // Archive test reports
            junit testResults: 'target/surefire-reports/*.xml', 
                  allowEmptyResults: true,
                  skipPublishingChecks: true

            archiveArtifacts artifacts: '''
                target/surefire-reports/**,
                target/allure-results/**,
                target/reports/**,
                logs/** 
            ''', allowEmptyArchive: true
        }

        success {
            allure([
                includeProperties: false,
                jdk: '',
                results: [[path: 'target/allure-results']],
                reportBuildPolicy: 'ALWAYS',
                properties: []
            ])
            echo "✅ Build & Tests Passed Successfully!"
        }

        unstable {
            allure([
                includeProperties: false,
                jdk: '',
                results: [[path: 'target/allure-results']]
            ])
            echo "⚠️  Build is Unstable (some tests failed)"
        }

        failure {
            allure([
                includeProperties: false,
                jdk: '',
                results: [[path: 'target/allure-results']]
            ])
            echo "❌ Build Failed. Check console output and reports."
        }
    }
}