FROM amazoncorretto:23-alpine3.17-jdk
ARG JAR_FILE=/build/libs/transaction-*-SNAPSHOT.jar
WORKDIR /app
COPY ${JAR_FILE} /app/app.jar
ENTRYPOINT ["java", "-jar", "./app.jar"]
