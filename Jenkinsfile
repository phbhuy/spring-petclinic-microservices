pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                script {
                    echo "Building service..."
                    dir('spring-petclinic-vets-service') {
                        sh './mvnw clean install -DskipTests'
                    }
                }
            }
        }
        stage('Test') {
            steps {
                script {
                    echo "Running tests..."
                    dir('spring-petclinic-vets-service') {
                        sh './mvnw test'
                    }
                }
            }
        }
    }
    post {
        always {
            // Hiển thị kết quả test
            junit '**/target/test-*.xml'
            // Hiển thị báo cáo độ phủ từ JaCoCo
            jacoco changeBuildStatus: true, execPattern: '**/target/*.exec', classPattern: '**/target/classes', sourcePattern: '**/src/main/java'
        }
    }
}
