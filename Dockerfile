FROM maven:3.8.6-openjdk-11-slim AS builder
WORKDIR /usr/src/amusic-new-backend-gateway
COPY . .
RUN mvn clean install -Dmaven.test.skip

#app
FROM openjdk:11.0.15-oraclelinux7 
#WORKDIR /app
COPY --from=builder /usr/src/amusic-new-backend-gateway/target/gateway*.jar /app/backend-gateway.jar
CMD ["java", "-jar", "/app/backend-gateway.jar"]
