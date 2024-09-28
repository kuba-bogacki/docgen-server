FROM maven:3.8.3-openjdk-17 as builder
WORKDIR /docgenserver
COPY pom.xml .
COPY api-gateway/pom.xml ./api-gateway
COPY authentication-service/pom.xml ./authentication-service
COPY company-service/pom.xml ./company-service
COPY discovery-server/pom.xml ./discovery-server
COPY document-service/pom.xml ./document-service
COPY event-service/pom.xml ./event-service
COPY notification-service/pom.xml ./notification-service
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -pl event-service -am

FROM openjdk:17-alpine
WORKDIR /docgenserver
COPY --from=builder /docgenserver/event-service/target/event-service-1.0-SNAPSHOT.jar ./
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5000", "-jar", "event-service-1.0-SNAPSHOT.jar"]


#FROM openjdk:17-alpine
#ADD target/event-service-1.0-SNAPSHOT.jar event-service-1.0-SNAPSHOT.jar
#ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5003", "-jar", "event-service-1.0-SNAPSHOT.jar"]