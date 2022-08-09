FROM maven:3.8.6-jdk-11 AS MAVEN_BUILD

COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build/

RUN --mount=type=cache,target=/root/.m2 mvn -Dmaven.test.skip=true package -Ptest


FROM amazoncorretto:11

WORKDIR /app

COPY --from=MAVEN_BUILD /build/target/zookeeper-0.0.1-SNAPSHOT.jar /app/

ENTRYPOINT ["java","-jar","zookeeper-0.0.1-SNAPSHOT.jar"]
