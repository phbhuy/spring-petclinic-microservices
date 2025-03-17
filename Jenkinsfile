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
                    // Kiểm tra sự thay đổi trong các thư mục microservice
                    def changedFiles = sh(script: "git diff --name-only HEAD~1", returnStdout: true).trim()
                    echo "Changed files: ${changedFiles}"

                    // Kiểm tra các thư mục dịch vụ cụ thể có thay đổi
                    def shouldBuildCustomerService = changedFiles.contains("spring-petclinic-customers-service")
                    def shouldBuildVetsService = changedFiles.contains("spring-petclinic-vets-service")
                    def shouldBuildVisitsService = changedFiles.contains("spring-petclinic-visits-service")

                    // Nếu lần đầu chạy hoặc có thay đổi trong dịch vụ, build và test
                    def isFirstRun = !fileExists('first-run.marker')

                    if (isFirstRun) {
                        echo 'First run detected, building and testing all services.'
                        // Tạo tệp marker để đánh dấu rằng đây là lần đầu chạy
                        sh 'touch first-run.marker'
                        // Build và test tất cả các service
                        buildAllServices()
                    } else {
                        // Chạy build và test chỉ cho các dịch vụ có thay đổi
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

                        // Nếu không có thay đổi, thông báo bỏ qua build
                        if (!shouldBuildCustomerService && !shouldBuildVetsService && !shouldBuildVisitsService) {
                            echo 'No changes detected in services, skipping build.'
                        }
                    }
                }
            }
        }
    }

    // post {
    //     always {
    //         // Không xóa workspace sau khi pipeline hoàn thành
    //         // cleanWs()  // Bỏ qua hoặc xóa dòng này để không dọn dẹp workspace
    //     }
    // }
}

def buildService(serviceName) {
    // Build và test từng service dựa trên tên của service
    echo "Building and testing service: ${serviceName}"
    sh "cd ${serviceName} && ./mvnw clean install -DskipTests"
    sh "cd ${serviceName} && ./mvnw clean test"
    junit "**/${serviceName}/target/surefire-reports/*.xml"
    jacoco()
}

def buildAllServices() {
    // Build và test tất cả các dịch vụ
    echo 'Building and testing all services...'
    buildService('spring-petclinic-customers-service')
    buildService('spring-petclinic-vets-service')
    buildService('spring-petclinic-visits-service')
}
