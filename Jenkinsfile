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

        stage('Build & Test with Maven') {
            steps {
                script {
                    echo "Building and testing with Maven..."
                    sh """
                        ./mvnw clean package -DskipTests=false
                    """
                }
            }
        }

        stage('Prepare Build Metadata') {
            steps {
                script {
                    def getGitBranchName = {
                        return scm.branches[0].name.replaceAll('^origin/', '').trim()
                    }

                    def branch = getGitBranchName()
                    def commitId = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()

                    echo "Branch: ${branch}"
                    echo "Commit: ${commitId}"

                    env.IMAGE_TAG = (branch == 'main') ? 'latest' : commitId
                    echo "Tag image: ${env.IMAGE_TAG}"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    echo "Building Docker image..."
                    sh """
                        docker build -t ${DOCKER_REPO}:${IMAGE_TAG} -f docker/Dockerfile .
                    """
                }
            }
        }

        stage('Push to DockerHub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: "${DOCKER_CRED_ID}",
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    script {
                        echo "Pushing Docker image to DockerHub..."
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
}
