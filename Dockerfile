FROM maven:3.8.5-openjdk-11-slim as build

COPY $PWD /mpiproxy
WORKDIR /mpiproxy

RUN mvn install

FROM gcr.io/distroless/java11-debian11
COPY --from=build /mpiproxy/target/mpiproxy-*.jar /app/mpiproxy.jar
CMD ["/app/mpiproxy.jar"]