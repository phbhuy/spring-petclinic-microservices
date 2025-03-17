pipeline {
    agent any

    environment {
        // Đảm bảo rằng Jenkins có quyền truy cập vào Maven Wrapper
        MAVEN_OPTS = "-Dmaven.repo.local=$WORKSPACE/.m2/repository"
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout code từ Git repository
                checkout scm
            }
        }

        stage('Build and Test - Customer Service') {
            steps {
                script {
                    dir('spring-petclinic-customers-service') {
                        // Build và test cho customer-service
                        sh './mvnw clean install -DskipTests'
                    }
                }
            }
        }

        stage('Build and Test - Vets Service') {
            steps {
                script {
                    dir('spring-petclinic-vets-service') {
                        // Build và test cho vets-service
                        sh './mvnw clean install -DskipTests'
                    }
                }
            }
        }

        stage('Build and Test - Visits Service') {
            steps {
                script {
                    dir('spring-petclinic-visits-service') {
                        // Build và test cho visits-service
                        sh './mvnw clean install -DskipTests'
                    }
                }
            }
        }
    }

    post {
        always {
            // Cleanup sau khi pipeline chạy xong
            cleanWs()
        }
    }
}
