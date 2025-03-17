pipeline {
    agent any

    environment {
        // Danh sách các microservices trong dự án
        SERVICES = ['spring-petclinic-api-gateway', 'spring-petclinic-customers-service', 'spring-petclinic-vets-service', 'spring-petclinic-visits-service']
    }

    stages {
        stage('Check for Changes') {
            steps {
                script {
                    // Kiểm tra thay đổi trong các thư mục của microservices
                    def changedServices = []
                    SERVICES.each { service ->
                        // Lấy danh sách các file thay đổi giữa commit gần nhất
                        def changedFiles = sh(script: "git diff --name-only HEAD~1..HEAD", returnStdout: true).trim()
                        
                        // Kiểm tra nếu thay đổi có liên quan đến service nào
                        if (changedFiles.contains(service)) {
                            changedServices.add(service)
                        }
                    }
                    // Lưu các dịch vụ đã thay đổi để sử dụng trong các bước tiếp theo
                    env.CHANGED_SERVICES = changedServices.join(',')
                }
            }
        }

        stage('Build') {
            parallel {
                // Build cho mỗi service chỉ khi có thay đổi
                stage('Build API Gateway') {
                    when {
                        expression { env.CHANGED_SERVICES.contains('spring-petclinic-api-gateway') }
                    }
                    steps {
                        echo "Building API Gateway..."
                        dir('spring-petclinic-api-gateway') {
                            sh './mvnw clean install -DskipTests'
                        }
                    }
                }
                stage('Build Customers Service') {
                    when {
                        expression { env.CHANGED_SERVICES.contains('spring-petclinic-customers-service') }
                    }
                    steps {
                        echo "Building Customers Service..."
                        dir('spring-petclinic-customers-service') {
                            sh './mvnw clean install -DskipTests'
                        }
                    }
                }
                stage('Build Vets Service') {
                    when {
                        expression { env.CHANGED_SERVICES.contains('spring-petclinic-vets-service') }
                    }
                    steps {
                        echo "Building Vets Service..."
                        dir('spring-petclinic-vets-service') {
                            sh './mvnw clean install -DskipTests'
                        }
                    }
                }
                stage('Build Visits Service') {
                    when {
                        expression { env.CHANGED_SERVICES.contains('spring-petclinic-visits-service') }
                    }
                    steps {
                        echo "Building Visits Service..."
                        dir('spring-petclinic-visits-service') {
                            sh './mvnw clean install -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Test') {
            parallel {
                // Test cho mỗi service chỉ khi có thay đổi
                stage('Test API Gateway') {
                    when {
                        expression { env.CHANGED_SERVICES.contains('spring-petclinic-api-gateway') }
                    }
                    steps {
                        echo "Running tests for API Gateway..."
                        dir('spring-petclinic-api-gateway') {
                            sh './mvnw test'
                        }
                    }
                }
                stage('Test Customers Service') {
                    when {
                        expression { env.CHANGED_SERVICES.contains('spring-petclinic-customers-service') }
                    }
                    steps {
                        echo "Running tests for Customers Service..."
                        dir('spring-petclinic-customers-service') {
                            sh './mvnw test'
                        }
                    }
                }
                stage('Test Vets Service') {
                    when {
                        expression { env.CHANGED_SERVICES.contains('spring-petclinic-vets-service') }
                    }
                    steps {
                        echo "Running tests for Vets Service..."
                        dir('spring-petclinic-vets-service') {
                            sh './mvnw test'
                        }
                    }
                }
                stage('Test Visits Service') {
                    when {
                        expression { env.CHANGED_SERVICES.contains('spring-petclinic-visits-service') }
                    }
                    steps {
                        echo "Running tests for Visits Service..."
                        dir('spring-petclinic-visits-service') {
                            sh './mvnw test'
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            junit '**/target/test-*.xml'  // Quét kết quả test
            cobertura '**/target/cobertura-coverage.xml' // Quét độ phủ test
        }
    }
}
