FROM amazoncorretto:17

ADD analyzer-0.0.1-SNAPSHOT.jar /app/analyzer.jar

ENTRYPOINT ["java","-jar","app/analyzer.jar"]
