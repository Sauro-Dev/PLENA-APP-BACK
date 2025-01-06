FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/sgt-0.0.1.jar
COPY ${JAR_FILE} sgt.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","sgt.jar"]