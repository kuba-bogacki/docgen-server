FROM maven:3.8.3-openjdk-17 AS builder
WORKDIR /docgenserver

COPY pom.xml .
COPY api-gateway/pom.xml ./api-gateway/
COPY authentication-service/pom.xml ./authentication-service/
COPY company-service/pom.xml ./company-service/
COPY discovery-server/pom.xml ./discovery-server/
COPY document-service/pom.xml ./document-service/
COPY event-service/pom.xml ./event-service/
#COPY kafka-service/pom.xml ./kafka-service/
COPY notification-service/pom.xml ./notification-service/

RUN mvn dependency:go-offline
COPY . .
RUN mvn clean compile -Dmaven.test.skip && mvn clean package -Dmaven.test.skip

FROM amazoncorretto:17-alpine-jdk
WORKDIR /notification-service
COPY --from=builder /docgenserver/notification-service/target/notification-service-1.0-SNAPSHOT.jar ./
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "notification-service-1.0-SNAPSHOT.jar"]