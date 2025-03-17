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

        stage('Check for Changes') {
            steps {
                script {
                    // Lệnh git diff để kiểm tra sự thay đổi trong các thư mục microservice
                    def changedFiles = sh(script: "git diff --name-only HEAD~1", returnStdout: true).trim()
                    
                    // Kiểm tra xem có thay đổi trong thư mục của từng service
                    def shouldBuildCustomerService = changedFiles.contains("spring-petclinic-customers-service")
                    def shouldBuildVetsService = changedFiles.contains("spring-petclinic-vets-service")
                    def shouldBuildVisitsService = changedFiles.contains("spring-petclinic-visits-service")

                    // Build và test cho service có thay đổi
                    if (shouldBuildCustomerService) {
                        echo 'Changes detected in customer-service, building and testing...'
                        buildService('spring-petclinic-customers-service')
                    }

                    if (shouldBuildVetsService) {
                        echo 'Changes detected in vets-service, building and testing...'
                        buildService('spring-petclinic-vets-service')
                    }

                    if (shouldBuildVisitsService) {
                        echo 'Changes detected in visits-service, building and testing...'
                        buildService('spring-petclinic-visits-service')
                    }

                    // Nếu không có thay đổi trong thư mục, thông báo và bỏ qua build
                    if (!shouldBuildCustomerService && !shouldBuildVetsService && !shouldBuildVisitsService) {
                        echo 'No changes detected in services, skipping build.'
                    }
                }
            }
        }
    }

    post {
        always {
            // Không xóa workspace sau khi chạy xong pipeline
            // cleanWs()  // Bỏ qua hoặc xóa dòng này để không dọn dẹp workspace
        }
    }
}

def buildService(serviceName) {
    // Build và test từng service dựa trên tên của service
    sh "cd ${serviceName} && ./mvnw clean install -DskipTests"
    sh "cd ${serviceName} && ./mvnw clean test"
    junit "**/${serviceName}/target/surefire-reports/*.xml"
    jacoco()
}
