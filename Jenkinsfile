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

        stage('Test') {
            steps {
                script {
                    // Chạy Maven Wrapper để thực hiện các test và tạo báo cáo độ phủ
                    sh './mvnw clean test'
                }
            }
            post {
                success {
                    // Upload kết quả test vào Jenkins
                    junit '**/target/test-classes/*.xml'  // Đảm bảo rằng kết quả test có ở đúng vị trí
                    jacoco()
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    // Chạy Maven Wrapper để build ứng dụng
                    sh './mvnw clean install -DskipTests'
                }
            }
            post {
                success {
                    echo 'Build thành công!'
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
