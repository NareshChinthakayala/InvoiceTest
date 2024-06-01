FROM eclipse-temurin:21-jdk
MAINTAINER "harsha"
COPY target/Invoice-0.0.1-SNAPSHOT.jar  /usr/app/
WORKDIR /usr/app/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "Invoice-0.0.1-SNAPSHOT.jar"]