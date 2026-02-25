pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    parameters {
        string(name: 'TEST_TAGS', defaultValue: '@integration', description: 'Cucumber tags to execute')
        choice(name: 'TEST_ENV', choices: ['jenkins', 'qa'], description: 'Configuration environment')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run browser in headless mode')
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                script {
                    if (isUnix()) {
                        sh "mvn -B clean test -Denv=${params.TEST_ENV} -Dheadless=${params.HEADLESS} -Dcucumber.filter.tags='${params.TEST_TAGS}'"
                    } else {
                        bat "mvn -B clean test -Denv=%TEST_ENV% -Dheadless=%HEADLESS% -Dcucumber.filter.tags=%TEST_TAGS%"
                    }
                }
            }
        }
    }

    post {
    always {
        // Publish JUnit results
        junit allowEmptyResults: true, testResults: 'target/surefire-reports/TEST-*.xml'
        
        // Generate and Publish the Allure HTML Report natively in Jenkins
        allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']]
    }
    }
}
