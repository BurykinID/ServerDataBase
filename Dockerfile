#FROM java:alphine
#VOLUME
#RUN mkdir usr/src/app
#WORKDIR usr/src/app
#COPY . usr/src/app
#EXPOSE 8080
#CMD ["java", "demo.jar"]
#ENTRYPOINT ["java", "-jar", "demo.jar"]

#FROM openjdk:8-jdk-alpine
#VOLUME /tmp
#EXPOSE 8082
#RUN mkdir -p /app/
#RUN mkdir -p /app/logs/
#ADD target/demo.jar /app/app.jar
#ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/.urandom","-Dspring.profiles.active=container", "-jar", "/app/app.jar"]


FROM openjdk:8-jdk-alpine
MAINTAINER demo.example.com
VOLUME /tmp
EXPOSE 8080
ADD target/demo.jar demo.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/.urandom","-Dspring.profiles.active=container", "-jar", "/demo.jar"]