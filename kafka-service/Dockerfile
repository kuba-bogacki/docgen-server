#FROM maven:3.8.3-openjdk-17 as builder
#WORKDIR /docgenserver
#
#COPY pom.xml .
#COPY api-gateway/pom.xml ./api-gateway/
#COPY authentication-service/pom.xml ./authentication-service/
#COPY company-service/pom.xml ./company-service/
#COPY discovery-server/pom.xml ./discovery-server/
#COPY document-service/pom.xml ./document-service/
#COPY event-service/pom.xml ./event-service/
#COPY kafka-service/pom.xml ./kafka-service/
#COPY notification-service/pom.xml ./notification-service/
#
#RUN mvn dependency:go-offline
#COPY . .
#RUN mvn clean package -pl kafka-service -am -DskipTests
#
#FROM openjdk:17-alpine
#WORKDIR /kafka-service
#COPY --from=builder /docgenserver/kafka-service/target/kafka-service-1.0-SNAPSHOT.jar ./
#CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5000", "-jar", "kafka-service-1.0-SNAPSHOT.jar"]