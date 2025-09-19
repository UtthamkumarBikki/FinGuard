FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/expenseiq-0.0.1-SNAPSHOT.jar expenseiq-v1.0.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "expenseiq-v1.0.jar"]
