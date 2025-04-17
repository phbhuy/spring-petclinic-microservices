pipeline {
    agent any

    environment {
        DOCKER_REPO = 'phbhuy19/spring-petclinic-microservices'
        DOCKER_CRED_ID = 'dockerhub-cred'
        SERVICES = 'spring-petclinic-vets-service,spring-petclinic-visits-service,spring-petclinic-customers-service' // Danh sách các service dưới dạng chuỗi
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

                    env.IMAGE_TAG = (branch == 'main') ? 'latest' : commitId
                    echo "Tag image: ${env.IMAGE_TAG}"
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    def services = SERVICES.split(',') // Chuyển chuỗi thành danh sách
                    for (service in services) {
                        // Lấy tên file JAR cụ thể
                        def jarFile = sh(script: "ls ${service}/target/*.jar", returnStdout: true).trim()
                        echo "Building Docker image for service: ${service} with JAR file: ${jarFile}..."
                        def jarFileName = jarFile.tokenize('/').last()
                        def jarDir = jarFile.replace("/${jarFileName}", "")
                        sh """
                            docker build \
                                --build-arg ARTIFACT_NAME=${jarFileName} \
                                -t ${DOCKER_REPO}-${service}:${IMAGE_TAG} \
                                -f ${service}/Dockerfile ${jarDir}
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
                        for (service in services) {
                            echo "Pushing Docker image for service: ${service} to DockerHub..."
                            sh """
                                echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                                docker push ${DOCKER_REPO}-${service}:${IMAGE_TAG}
                                docker logout
                            """
                        }
                    }
                }
            }
        }
    }
}
