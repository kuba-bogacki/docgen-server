pipeline {
    agent any

    environment {
        DOCKER_CREDENTIALS = 'b05c57b9-a488-435e-bbc6-2a644dcc0114'
        DOCKER_REGISTRY = 'https://registry.hub.docker.com'
        DOCKER_NAMESPACE = 'spaceavocado34'
    }

    stages {
        stage('Checkout') {
            steps {
                git credentialsId: '171df50d-7844-49ee-b099-901765f37324', url: 'https://github.com/kuba-bogacki/docgen-server', branch: 'main'
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
                        def imageName = "${DOCKER_NAMESPACE}/${service}:1.0-SNAPSHOT"
                        sh """
                            docker build -t ${imageName} -f ${service}/Dockerfile .
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
                            def imageName = "${DOCKER_NAMESPACE}/${service}:1.0-SNAPSHOT"
                            docker.image(imageName).push()
                        }
                    }
                }
            }
        }
    }

    post {
        always {
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