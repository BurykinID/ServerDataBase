FROM openjdk:8-jdk-alpine
RUN mkdir -p usr/src/app/
RUN mkdir -p /var/lib/postgresql/data/
WORKDIR usr/src/app/
ADD target/demo.jar usr/src/app/demo.jar
EXPOSE 8080
#CMD ["java", "demo.jar"]
ENTRYPOINT ["java", "-jar", "usr/src/app/demo.jar"]

#FROM openjdk:8-jdk-alpine
#VOLUME /tmp
#EXPOSE 8082
#RUN mkdir -p /app/
#RUN mkdir -p /app/logs/
#ADD target/demo.jar /app/app.jar
#ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/.urandom","-Dspring.profiles.active=container", "-jar", "/app/app.jar"]


#FROM openjdk:8-jdk-alpine
#VOLUME /tmp
#RUN mkdir -p tmp/files
#EXPOSE 8080
#ADD target/demo.jar demo.jar
#ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/.urandom","-Dspring.profiles.active=container", "-jar", "/demo.jar"]