pipeline {
    agent any
    stages {
        stage('Check Java and Maven') {
            steps {
                sh 'java -version'
                sh './mvnw -version'  // Kiểm tra phiên bản Maven Wrapper từ thư mục gốc
            }
        }
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                // Chạy Maven Wrapper từ thư mục gốc
                sh './mvnw clean install -DskipTests'
            }
        }
    }
}
