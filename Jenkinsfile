pipeline {
    agent any

    tools {
        maven 'Maven 3.5.0'
        jdk 'Java 8'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }

    triggers {
        pollSCM '@daily'
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/dschadow/CloudSecurity'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B package'
            }
        }

        stage('Sonar') {
            steps {
                sh "mvn sonar:sonar -Dsonar.host.url=${env.SONARQUBE_HOST}"
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts(artifacts: '**/target/*.jar', fingerprint: true)
                junit(testResults: '**/target/**TEST*.xml', allowEmptyResults: true)
            }
        }
    }

    post {
        failure {
            emailext(
                    subject: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} Failure",
                    body: """<p>Job ${env.JOB_NAME} [${env.BUILD_NUMBER}] failed:</p>
                             <p>Check the console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME}/${
                        env.BUILD_NUMBER
                    }</a></p>""",
                    recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )

            slackSend(color: '#FF0000', message: "FAILURE: Job ${env.JOB_NAME} [${env.BUILD_NUMBER}]: ${env.BUILD_URL}")
        }
    }
}