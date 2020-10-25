FROM openjdk:8-jdk-alpine

ADD producer-app*.jar /producer-app.jar

ENTRYPOINT java -jar /producer-app.jar --spring.config.location=classpath:/docker.properties
