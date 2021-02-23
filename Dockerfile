FROM openjdk:11-jre
VOLUME /tmp
COPY ./build/docker/*.jar ./build/docker/image/app.jar
ENTRYPOINT ["java","-jar","./build/docker/image/app.jar"]
EXPOSE 8080