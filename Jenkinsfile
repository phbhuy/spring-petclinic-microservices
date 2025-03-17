pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                script {
                    echo "Building service..."
                    dir('spring-petclinic-vets-service') {
                        // Cấp quyền thực thi cho mvnw
                        sh 'chmod +x ./.mvn/wrapper/mvnw'
                        // Chạy mvnw với đúng đường dẫn
                        sh './.mvn/wrapper/mvnw clean install -DskipTests'
                    }
                }
            }
        }
        stage('Test') {
            steps {
                script {
                    echo "Running tests..."
                    dir('spring-petclinic-vets-service') {
                        // Chạy mvnw để test
                        sh './.mvn/wrapper/mvnw test'
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
