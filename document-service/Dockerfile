FROM maven:3.8.3-openjdk-17 as builder
WORKDIR /docgenserver

COPY pom.xml .
COPY pom.xml ./api-gateway/
COPY pom.xml ./authentication-service/
COPY pom.xml ./company-service/
COPY pom.xml ./discovery-server/
COPY pom.xml ./document-service/
COPY pom.xml ./event-service/
#COPY kafka-service/pom.xml ./kafka-service/
COPY pom.xml ./notification-service/

RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -pl document-service -am

FROM openjdk:17-alpine
WORKDIR /document-service
COPY --from=builder /docgenserver/document-service/target/document-service-1.0-SNAPSHOT.jar ./
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5000", "-jar", "document-service-1.0-SNAPSHOT.jar"]