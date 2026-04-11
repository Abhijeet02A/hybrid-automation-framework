pipeline {
    agent {
        docker { 
            image 'maven:3.9.5-eclipse-temurin-17' 
            args '-v /root/.m2:/root/.m2' // Persist Maven dependencies
        }
    }

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
