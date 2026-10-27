FROM maven:3.8.3-openjdk-17 AS builder
WORKDIR /docgenserver

COPY pom.xml .
COPY api-gateway/pom.xml ./api-gateway/
COPY authentication-service/pom.xml ./authentication-service/
COPY company-service/pom.xml ./company-service/
COPY discovery-server/pom.xml ./discovery-server/
COPY document-service/pom.xml ./document-service/
COPY event-service/pom.xml ./event-service/
COPY notification-service/pom.xml ./notification-service/

RUN mvn dependency:go-offline -B
COPY . .
RUN mvn clean package -pl discovery-server -am -Dmaven.test.skip=true

FROM amazoncorretto:17-alpine-jdk
WORKDIR /discovery-server
COPY --from=builder /docgenserver/discovery-server/target/*.jar ./discovery-server.jar
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:2137", "-jar", "discovery-server.jar"]
