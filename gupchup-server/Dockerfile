FROM maven:3.6.0-jdk-11 AS build

COPY . /build
WORKDIR build

RUN mvn verify

FROM openjdk:11.0.3-jre-slim

WORKDIR /

RUN mkdir /app
COPY --from=build /build/target/gupchup-server-*.jar /app/gupchup-server.jar

COPY --from=build /build/src/main/config/entrypoint.sh /entrypoint.sh
RUN chmod a+x /entrypoint.sh

EXPOSE 8080

ENTRYPOINT /entrypoint.sh
