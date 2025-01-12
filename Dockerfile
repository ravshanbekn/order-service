FROM openjdk:17-jdk-slim-buster
WORKDIR /app

COPY /build/libs/order-service-0.0.1-SNAPSHOT.jar build/

WORKDIR /app/build
EXPOSE 8080
ENTRYPOINT java -jar order-service-0.0.1-SNAPSHOT.jar