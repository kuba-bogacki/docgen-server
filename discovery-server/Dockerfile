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
RUN mvn clean package -pl discovery-server -am

FROM openjdk:17-alpine
WORKDIR /discovery-server
COPY --from=builder /docgenserver/discovery-server/target/discovery-server-1.0-SNAPSHOT.jar ./
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5000", "-jar", "discovery-server-1.0-SNAPSHOT.jar"]
