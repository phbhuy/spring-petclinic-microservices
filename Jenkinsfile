pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                script {
                    echo "Building service..."
                    dir('spring-petclinic-vets-service') {
                        // Sử dụng đường dẫn tới maven-wrapper.jar và chạy bằng Java
                        sh 'java -jar ./.mvn/wrapper/maven-wrapper.jar clean install -DskipTests'
                    }
                }
            }
        }
        stage('Test') {
            steps {
                script {
                    echo "Running tests..."
                    dir('spring-petclinic-vets-service') {
                        // Sử dụng đường dẫn tới maven-wrapper.jar và chạy bằng Java
                        sh 'java -jar ./.mvn/wrapper/maven-wrapper.jar test'
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
