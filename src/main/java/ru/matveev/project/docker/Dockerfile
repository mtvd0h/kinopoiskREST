FROM openjdk:21-oraclelinux9
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} finalApplication.jar
ENTRYPOINT ["java", "-jar", "finalApplication.jar"]