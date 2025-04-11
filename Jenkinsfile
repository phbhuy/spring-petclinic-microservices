pipeline {
    agent any

    environment {
        DOCKER_REPO = 'phbhuy19/spring-petclinic-microservices'
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
                    // Dùng biến môi trường BRANCH_NAME có sẵn trong Jenkins
                    def branch = env.BRANCH_NAME?.replaceFirst(/^origin\//, '')?.trim()
                    def commitId = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()

                    // Debug xem lấy đúng branch chưa
                    echo "▶️ Branch: ${branch}"
                    echo "🔖 Commit: ${commitId}"

                    // Gán tag theo yêu cầu
                    env.IMAGE_TAG = (branch == 'main') ? 'main' : commitId
                    echo "📦 Tag image: ${env.IMAGE_TAG}"
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

