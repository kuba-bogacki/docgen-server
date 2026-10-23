pipeline {
    agent any

    environment {
        DOCKER_CREDENTIALS = 'a7d50e5c-36b3-464f-aa85-81d0a498ce09'
        DOCKER_REGISTRY = 'https://registry.hub.docker.com'
        DOCKER_NAMESPACE = 'spaceavocado34'
    }

    stages {
        stage('Checkout') {
            steps {
                git credentialsId: 'd1a07e22-e482-4351-88a6-2f40c9a760a9',
                    url: 'https://github.com/kuba-bogacki/docgen-server',
                    branch: 'main'
                script {
                    def shortCommit = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    env.IMAGE_TAG = "${env.BUILD_NUMBER}-${shortCommit}"
                }
            }
        }

        stage('Build with Maven') {
            steps {
                script {
                    def services = ['discovery-server', 'authentication-service', 'company-service', 'notification-service', 'document-service', 'event-service', 'api-gateway']
                    for (service in services) {
                        sh "mvn clean package -pl ${service} -am -DskipTests"
                    }
                }
            }
        }

        stage('Build Test') {
            steps {
                script {
                    def services = ['discovery-server', 'authentication-service', 'company-service', 'notification-service', 'document-service', 'event-service', 'api-gateway']
                    for (service in services) {
                        sh "mvn test -pl ${service}"
                    }
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    def services = ['discovery-server', 'authentication-service', 'company-service', 'notification-service', 'document-service', 'event-service', 'api-gateway']
                    for (service in services) {
                        def imageName = "${DOCKER_NAMESPACE}/${service}:${env.IMAGE_TAG}"
                        def latestName = "${DOCKER_NAMESPACE}/${service}:latest"
                        sh """
                            docker build -t ${imageName} -t ${latestName} -f ${service}/Dockerfile .
                        """
                    }
                }
            }
        }

        stage('Push Docker Images') {
            steps {
                script {
                    docker.withRegistry("${DOCKER_REGISTRY}", "${DOCKER_CREDENTIALS}") {
                        def services = ['discovery-server', 'authentication-service', 'company-service', 'notification-service', 'document-service', 'event-service', 'api-gateway']
                        for (service in services) {
                            def imageName = "${DOCKER_NAMESPACE}/${service}:${env.IMAGE_TAG}"
                            def latestName = "${DOCKER_NAMESPACE}/${service}:latest"
                            docker.image(imageName).push()
                            docker.image(latestName).push()
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
            sh 'docker system prune -af'
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}