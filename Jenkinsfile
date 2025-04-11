pipeline {
    agent any

    environment {
        DOCKER_REPO = 'phbhuy19/spring-petclinic-microservices'   // Thay bằng đúng repo của bạn
        DOCKER_CRED_ID = 'dockerhub-cred'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                script {
                    // Lấy tên branch
                    def branch = sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()
                    def commitId = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()

                    // Nếu là branch main → tag main
                    // Ngược lại → tag commit id
                    env.IMAGE_TAG = (branch == 'main') ? 'main' : commitId
                }

                sh """
                    docker build -t ${DOCKER_REPO}:${IMAGE_TAG} -f docker/Dockerfile .
                """

                withCredentials([usernamePassword(
                    credentialsId: "${DOCKER_CRED_ID}",
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                        docker push ${DOCKER_REPO}:${IMAGE_TAG}
                        docker logout
                    '''
                }
            }
        }
    }
}
