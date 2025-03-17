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

        stage('Build') {
            steps {
                script {
                    // Chạy Maven Wrapper để build ứng dụng mà không chạy tests
                    sh './mvnw clean install -DskipTests'
                }
            }
            post {
                success {
                    echo 'Build thành công!'
                }
                failure {
                    echo 'Build thất bại!'
                }
            }
        }
    }

    post {
        always {
            // Bước này sẽ luôn được thực thi bất kể thành công hay thất bại
            cleanWs()
        }
    }
}
