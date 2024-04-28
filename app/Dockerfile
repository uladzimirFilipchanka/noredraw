FROM eclipse-temurin:21
VOLUME /source
COPY ./build/libs/app-*.jar /app/app.jar
WORKDIR /source
ENTRYPOINT ["java", "-jar","/app/app.jar"]
