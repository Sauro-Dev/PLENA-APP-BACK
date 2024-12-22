FROM openjdk:17-jdk-slim
ARG jAR_FILE=target/sgt-0.0.1.jar
COPY ${jAR_FILE} sgt.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","sgt.jar"]