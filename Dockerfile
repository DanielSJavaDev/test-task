FROM amazoncorretto:17-alpine-jdk
VOLUME /tmp
ARG JAR_FILE=test/target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]