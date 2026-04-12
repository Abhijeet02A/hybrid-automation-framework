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
                    // grid.host is 'selenium-hub' because Jenkins and Grid are on the same Docker network
                    def mvnCmd = "mvn clean verify -DrunMode=remote -Dgrid.host=selenium-hub " +
                                 "-Dbrowser=${params.BROWSER} -Dheadless=${params.HEADLESS} " +
                                 "-Dcucumber.filter.tags='${params.TEST_TAGS}'"

                    sh mvnCmd
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
                // Allure report only if build succeeds
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
