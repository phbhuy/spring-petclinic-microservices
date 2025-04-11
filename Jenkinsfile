pipeline {
    agent any

    environment {
        MAVEN_OPTS = "-Dmaven.repo.local=$WORKSPACE/.m2/repository"
        DOCKER_REPO = 'phbhuy19/spring-petclinic-microservices'  // 👈 repo bạn trên Docker Hub
        DOCKER_CRED_ID = 'dockerhub-cred'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build with Maven') {
            steps {
                sh './mvnw clean install -DskipTests'
            }
        }

        stage('Run Unit Tests') {
            steps {
                sh './mvnw test'
            }
            post {
                success {
                    junit '**/target/surefire-reports/*.xml'
                }
                failure {
                    echo '❌ Unit Test thất bại!'
                }
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                script {
                    def commitId = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    env.IMAGE_TAG = commitId
                }

                // 🛠️ Sửa ở đây: chỉ định Dockerfile + context là thư mục gốc
                sh '''
                    docker build -f docker/Dockerfile -t ${DOCKER_REPO}:${IMAGE_TAG} .
                '''

                withCredentials([usernamePassword(credentialsId: "${DOCKER_CRED_ID}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                        docker push ${DOCKER_REPO}:${IMAGE_TAG}
                        docker logout
                    '''
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
