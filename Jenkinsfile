pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    parameters {
        string(name: 'TEST_TAGS', defaultValue: '@integration', description: 'Cucumber tags to execute')
        choice(name: 'BROWSER', choices: ['chrome', 'firefox'], description: 'Target Browser')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run browser in headless mode')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test Execution') {
            steps {
                script {
                    // EXPLICIT PATH RESOLUTION: This is the missing piece
                    def mvnHome = tool name: 'maven3', type: 'maven'
                    def javaHome = tool name: 'jdk17', type: 'jdk'
                    
                    withEnv(["PATH+MAVEN=${mvnHome}/bin", "JAVA_HOME=${javaHome}"]) {
                        def mvnCmd = "mvn clean verify -DrunMode=remote -Dgrid.host=selenium-hub " +
                                     "-Dbrowser=${params.BROWSER} -Dheadless=${params.HEADLESS} " +
                                     "-Dcucumber.filter.tags='${params.TEST_TAGS}'"

                        if (isUnix()) {
                            sh mvnCmd
                        } else {
                            bat mvnCmd
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
        }
        success {
            script {
                catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                    allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']]
                }
            }
        }
        failure {
            echo "Test execution failed. Check test reports in target/surefire-reports/"
        }
    }
}