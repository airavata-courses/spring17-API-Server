FROM maven:3.3.9-jdk-8-onbuild-alpine

CMD ["java", "-jar", "mock-airavata-api-server/target/mock-airavata-api-server-0.15-SNAPSHOT.jar"]
