pipeline {
    agent any

    environment {
        DOCKER_REPO = 'phbhuy19/spring-petclinic-microservices'
        DOCKER_CRED_ID = 'dockerhub-cred'
        SERVICES = 'spring-petclinic-config-server,spring-petclinic-discovery-server,spring-petclinic-api-gateway,spring-petclinic-admin-server,spring-petclinic-genai-service,spring-petclinic-vets-service,spring-petclinic-visits-service,spring-petclinic-customers-service'
        ROOT_FILES = 'pom.xml,docker,Dockerfile'   // Những file thay đổi sẽ build toàn bộ
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Detect Changes') {
            steps {
                script {
                    def changedFiles = sh(script: "git diff --name-only origin/main", returnStdout: true).trim().split("\n")
                    echo "Changed Files: ${changedFiles}"

                    def servicesChanged = [] as Set
                    def buildAll = false

                    for (file in changedFiles) {
                        if (ROOT_FILES.split(',').any { rootFile -> file.startsWith(rootFile) }) {
                            buildAll = true
                            break
                        }
                        SERVICES.split(',').each { svc ->
                            if (file.startsWith(svc)) {
                                servicesChanged.add(svc)
                            }
                        }
                    }

                    if (buildAll) {
                        echo "Detected root-level change ➜ Build ALL services"
                        env.TARGET_SERVICES = SERVICES
                    } else {
                        env.TARGET_SERVICES = servicesChanged.join(',')
                    }

                    echo "Services to Build: ${env.TARGET_SERVICES}"
                }
            }
        }

        stage('Prepare Build Metadata') {
            steps {
                script {
                    def branch = sh(script: "git rev-parse --abbrev-ref HEAD", returnStdout: true).trim()
                    def commitId = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    env.TAG_PART = (branch == 'main') ? 'latest' : commitId
                    env.BRANCH_NAME = branch
                }
            }
        }

        stage('Build Docker Images') {
            when {
                expression { env.TARGET_SERVICES }
            }
            steps {
                script {
                    def services = env.TARGET_SERVICES.split(',')
                    for (service in services) {
                        def jarPath = sh(script: "ls ${service}/target/*.jar", returnStdout: true).trim()
                        def fullTag = "${BRANCH_NAME}-${service}-${TAG_PART}"

                        sh """
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
            when {
                expression { env.TARGET_SERVICES }
            }
            steps {
                withCredentials([usernamePassword(
                    credentialsId: "${DOCKER_CRED_ID}",
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    script {
                        sh "echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin"
                        def services = env.TARGET_SERVICES.split(',')
                        for (service in services) {
                            def fullTag = "${BRANCH_NAME}-${service}-${TAG_PART}"
                            sh "docker push ${DOCKER_REPO}:${fullTag}"
                        }
                        sh "docker logout"
                    }
                }
            }
        }
    }
}
