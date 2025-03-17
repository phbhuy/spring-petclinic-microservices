pipeline {
    agent any
    stages {
        stage('Build All Microservices') {
            parallel {
                stage('Build API Gateway') {
                    steps {
                        dir('spring-petclinic-api-gateway') {
                            echo 'Building API Gateway...'
                            sh './mvnw clean install'
                        }
                    }
                }
                stage('Build Customers Service') {
                    steps {
                        dir('spring-petclinic-customers-service') {
                            echo 'Building Customers Service...'
                            sh './mvnw clean install'
                        }
                    }
                }
                stage('Build Vets Service') {
                    steps {
                        dir('spring-petclinic-vets-service') {
                            echo 'Building Vets Service...'
                            sh './mvnw clean install'
                        }
                    }
                }
                stage('Build Visits Service') {
                    steps {
                        dir('spring-petclinic-visits-service') {
                            echo 'Building Visits Service...'
                            sh './mvnw clean install'
                        }
                    }
                }
            }
        }
        stage('Test All Microservices') {
            parallel {
                stage('Test API Gateway') {
                    steps {
                        dir('spring-petclinic-api-gateway') {
                            echo 'Running tests for API Gateway...'
                            sh './mvnw test'
                        }
                    }
                }
                stage('Test Customers Service') {
                    steps {
                        dir('spring-petclinic-customers-service') {
                            echo 'Running tests for Customers Service...'
                            sh './mvnw test'
                        }
                    }
                }
                stage('Test Vets Service') {
                    steps {
                        dir('spring-petclinic-vets-service') {
                            echo 'Running tests for Vets Service...'
                            sh './mvnw test'
                        }
                    }
                }
                stage('Test Visits Service') {
                    steps {
                        dir('spring-petclinic-visits-service') {
                            echo 'Running tests for Visits Service...'
                            sh './mvnw test'
                        }
                    }
                }
            }
        }
    }
}
