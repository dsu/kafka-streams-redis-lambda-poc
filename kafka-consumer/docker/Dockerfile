FROM openjdk:8-jdk-alpine

ADD consumer-app*.jar /consumer-app.jar

ENTRYPOINT java -jar /consumer-app.jar --spring.config.location=classpath:/docker.properties
