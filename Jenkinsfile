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
                    junit '**/target/surefire-reports/*.xml'
                    jacoco()  // Báo cáo độ phủ
                }
                failure {
                    echo 'Test thất bại!'
                }
            }
        }

        stage('Check Coverage') {
            steps {
                script {
                    // Đọc độ phủ từ báo cáo JaCoCo
                    def coverage = sh(script: "grep -oP 'TOTAL\s+\K\d+(?=%)' target/site/jacoco/index.html", returnStdout: true).trim()
                    echo "Test coverage is: ${coverage}%"

                    // Nếu độ phủ dưới 70%, dừng pipeline
                    if (coverage.toFloat() < 70) {
                        error("Test coverage is below 70%, failing the build.")
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()  // Dọn dẹp workspace sau khi pipeline hoàn thành
        }
    }
}
