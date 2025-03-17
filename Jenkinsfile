pipeline {
    agent any
    environment {
        MAVEN_OPTS = "-Dmaven.repo.local=$WORKSPACE/.m2/repository"
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh './mvnw clean install -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh './mvnw clean test'
            }
            post {
                success {
                    // Đảm bảo rằng báo cáo test có ở đúng vị trí
                    junit '**/target/surefire-reports/*.xml'
                    jacoco() // Nếu có sử dụng JaCoCo để kiểm tra độ phủ
                }
                failure {
                    echo 'Test thất bại!'
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
