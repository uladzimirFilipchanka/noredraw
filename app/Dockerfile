FROM eclipse-temurin:21
VOLUME /source
COPY ./build/libs/noredraw-*.jar /app/noredraw.jar
WORKDIR /source
ENTRYPOINT ["java", "-jar","/app/noredraw.jar"]
