pipeline {
    agent any

    environment {
        // Docker Hub credentials ID
        DOCKER_CREDENTIALS = 'b05c57b9-a488-435e-bbc6-2a644dcc0114'
        // Docker Registry URL (e.g., Docker Hub, AWS ECR)
        DOCKER_REGISTRY = 'docker.io'
        // Docker Image Namespace (your Docker Hub username)
        DOCKER_NAMESPACE = 'spaceavocado34'
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout code from GitHub
                git credentialsId: '171df50d-7844-49ee-b099-901765f37324', url: 'https://github.com/kuba-bogacki/docgen-server', branch: 'main'
            }
        }

        stage('Build with Maven') {
            steps {
                // Use Maven to build the project and download dependencies
                script {
                    // Loop through each microservice directory
                    def services = ['discovery-server', 'authentication-service', 'company-service', 'notification-service', 'document-service', 'event-service', 'api-gateway', 'kafka-service']
                    for (service in services) {
                        sh "mvn clean package -pl ${service} -am -DskipTests"
                    }
                }
            }
        }

        stage('Build Test') {
            steps {
                script {
                    def services = ['discovery-server', 'authentication-service', 'company-service', 'notification-service', 'document-service', 'event-service', 'api-gateway', 'kafka-service']
                    for (service in services) {
                        sh "mvn test -pl ${service}"
                    }
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    def services = ['discovery-server', 'authentication-service', 'company-service', 'notification-service', 'document-service', 'event-service', 'api-gateway', 'kafka-service']
                    for (service in services) {
                        def imageName = "${DOCKER_NAMESPACE}/${service}:1.0-SNAPSHOT"
                        sh """
                            docker build -t ${imageName} ./${service}
                        """
                    }
                }
            }
        }

        stage('Push Docker Images') {
            steps {
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY}", "${DOCKER_CREDENTIALS}") {
                        def services = ['discovery-server', 'authentication-service', 'company-service', 'notification-service', 'document-service', 'event-service', 'api-gateway', 'kafka-service']
                        for (service in services) {
                            def imageName = "${DOCKER_NAMESPACE}/${service}:1.0-SNAPSHOT"
                            docker.image(imageName).push()
                        }
                    }
                }
            }
        }

//         stage('Deploy') {
//             steps {
//                 // Deploy to your environment. This could be a server with Docker Compose, Kubernetes, etc.
//                 // Example using Docker Compose on a remote server via SSH:
//                 script {
//                     // Define deployment server details
//                     def remoteUser = 'deploy-user'
//                     def remoteHost = 'your-deployment-server.com'
//                     def sshKey = 'your-ssh-credentials-id' // Stored in Jenkins Credentials
//
//                     // Execute Docker Compose commands on the remote server
//                     sh """
//                         ssh -o StrictHostKeyChecking=no -i ~/.ssh/id_rsa ${remoteUser}@${remoteHost} '
//                             cd /path/to/deployment/directory &&
//                             docker-compose pull &&
//                             docker-compose up -d --build
//                         '
//                     """
//                 }
//             }
//         }
    }

    post {
        always {
            // Clean up Docker images to save space
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