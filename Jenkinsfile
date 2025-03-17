pipeline {
    agent any
    stages {
        stage('Check Java and Maven') {
            steps {
                sh 'java -version'
                sh './mvnw -version'  // Sử dụng Maven Wrapper thay vì mvn
            }
        }
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                dir('spring-petclinic-vets-service') {
                    sh './mvnw clean install -DskipTests'  // Sử dụng Maven Wrapper
                }
            }
        }
    }
}
