FROM gradle:jdk17-focal AS build
WORKDIR /home/gradle/project

COPY gradle gradle
COPY gradlew gradlew.bat build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon

COPY src src
RUN ./gradlew build -x test --no-daemon --parallel

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UnlockExperimentalVMOptions -XX:+UseZGC"

COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]