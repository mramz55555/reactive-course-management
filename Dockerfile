FROM amazoncorretto:19 as build

COPY target/course-management-0.0.1-SNAPSHOT.jar course-management-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "course-management-0.0.1-SNAPSHOT.jar"]
