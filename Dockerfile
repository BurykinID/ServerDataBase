FROM openjdk:8-jdk-alpine
RUN mkdir -p usr/src/app/
RUN mkdir -p /var/lib/postgresql/data/
WORKDIR usr/src/app/
ADD target/demo.jar usr/src/app/demo.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "usr/src/app/demo.jar"]