FROM gcr.io/distroless/java17
WORKDIR /app
COPY target/imbed-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
