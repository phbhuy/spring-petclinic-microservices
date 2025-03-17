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
                // Chạy các unit test và in chi tiết test ra console
                sh './mvnw clean test -X'  // Thêm tùy chọn -X để hiển thị chi tiết hơn trong console log
            }
            post {
                success {
                    // Đảm bảo rằng báo cáo test có ở đúng vị trí
                    junit '**/target/surefire-reports/*.xml'  // Chỉ định file báo cáo test ở đây
                    jacoco()  // Nếu có sử dụng JaCoCo để kiểm tra độ phủ
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
