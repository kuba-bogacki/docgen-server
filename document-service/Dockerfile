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
RUN mvn clean package -pl document-service -am -Dmaven.test.skip=true

FROM amazoncorretto:17-alpine-jdk
WORKDIR /document-service
RUN apk add --no-cache fontconfig

COPY --from=builder /docgenserver/document-service/target/*.jar ./document-service.jar
COPY --from=builder /docgenserver/document-service/src/main/resources/fonts/ /usr/share/fonts/truetype/ptserif/

RUN fc-cache -f -v
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5004", "-jar", "document-service.jar"]