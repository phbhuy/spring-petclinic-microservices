pipeline {
    agent any

    environment {
        SERVICES = ['api-gateway', 'customers-service', 'vets-service', 'visits-service']
    }

    stages {
        stage('Check for Changes') {
            steps {
                script {
                    // Kiểm tra xem có thay đổi trong bất kỳ thư mục nào của service
                    def changedServices = []
                    SERVICES.each { service ->
                        def changedFiles = sh(script: "git diff --name-only HEAD~1..HEAD", returnStdout: true).trim()
                        if (changedFiles.contains(service)) {
                            changedServices.add(service)
                        }
                    }
                    // Lưu lại các service đã thay đổi để dùng trong các stages sau
                    env.CHANGED_SERVICES = changedServices.join(',')
                }
            }
        }

        stage('Build') {
            matrix {
                axes {
                    axis {
                        name 'SERVICE'
                        values 'api-gateway', 'customers-service', 'vets-service', 'visits-service'
                    }
                }
                stages {
                    stage('Build Service') {
                        when {
                            expression { env.CHANGED_SERVICES.contains(SERVICE) }
                        }
                        steps {
                            script {
                                echo "Building service: ${SERVICE}"
                                dir("spring-petclinic-${SERVICE}") {
                                    sh './mvnw clean install -DskipTests'
                                }
                            }
                        }
                    }
                }
            }
        }

        stage('Test') {
            matrix {
                axes {
                    axis {
                        name 'SERVICE'
                        values 'api-gateway', 'customers-service', 'vets-service', 'visits-service'
                    }
                }
                stages {
                    stage('Test Service') {
                        when {
                            expression { env.CHANGED_SERVICES.contains(SERVICE) }
                        }
                        steps {
                            script {
                                echo "Running tests for service: ${SERVICE}"
                                dir("spring-petclinic-${SERVICE}") {
                                    sh './mvnw test'
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    post {
        always {
            junit '**/target/test-*.xml' // Quét kết quả test
            cobertura '**/target/cobertura-coverage.xml' // Quét độ phủ test
        }
    }
}
