pipeline {
    agent any

    environment {
        DOCKER_REPO = 'phbhuy19/spring-petclinic-microservices'
        DOCKER_CRED_ID = 'dockerhub-cred'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: '**']],
                        userRemoteConfigs: [[
                            url: 'https://github.com/phbhuy/spring-petclinic-microservices.git',
                            refspec: '+refs/heads/*:refs/remotes/origin/*'
                        ]]
                    ])
                }
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                script {
                    def branch = sh(
                        script: "git name-rev --name-only HEAD",
                        returnStdout: true
                    ).trim()
                    def commitId = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()

                    echo "▶️ Branch: ${branch}"
                    echo "🔖 Commit: ${commitId}"

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
