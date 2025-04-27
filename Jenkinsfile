pipeline {
    agent any

    environment {
        DOCKER_REPO = 'phbhuy19/spring-petclinic-microservices'
        DOCKER_CRED_ID = 'dockerhub-cred'
        SERVICES = 'spring-petclinic-config-server,spring-petclinic-discovery-server,spring-petclinic-api-gateway,spring-petclinic-admin-server,spring-petclinic-genai-service,spring-petclinic-vets-service,spring-petclinic-visits-service,spring-petclinic-customers-service'
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
                    echo "Building and testing all services with Maven..."
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

                    def tagPart = (branch == 'main') ? 'latest' : commitId
                    env.BRANCH_NAME = branch
                    env.TAG_PART = tagPart

                    echo "Generated tag suffix: ${TAG_PART}"
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    def services = SERVICES.split(',') // Chuyển chuỗi thành danh sách
                    for (service in services) {
                        def jarPath = sh(script: "ls ${service}/target/*.jar", returnStdout: true).trim()
                        def jarFileName = jarPath.tokenize('/').last()

                        def fullTag = "${BRANCH_NAME}-${service}-${TAG_PART}"

                        echo "Building Docker image for service: ${service} with JAR: ${jarFileName} and tag: ${fullTag}"

                        sh """
                            if [ ! -f ${jarPath} ]; then
                                echo "Error: JAR file not found for service: ${service}!"
                                exit 1
                            fi
                            docker build \
                                --build-arg ARTIFACT_NAME=${jarPath} \
                                -t ${DOCKER_REPO}:${fullTag} \
                                -f ./docker/Dockerfile .
                        """
                    }
                }
            }
        }

        stage('Push Docker Images to DockerHub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: "${DOCKER_CRED_ID}",
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    script {
                        def services = SERVICES.split(',') // Chuyển chuỗi thành danh sách
                        sh """
                            echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin
                        """
                        for (service in services) {
                            def fullTag = "${BRANCH_NAME}-${service}-${TAG_PART}"
                            echo "Pushing Docker image for service: ${service} with tag: ${fullTag} to DockerHub..."
                            sh "docker push ${DOCKER_REPO}:${fullTag}"
                        }
                        sh "docker logout"
                    }
                }
            }
        }
    }
}
