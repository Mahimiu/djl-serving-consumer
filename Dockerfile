# docker build -t mosazhaw/djl-serving-consumer .
# docker push mosazhaw/djl-serving-consumer

FROM eclipse-temurin:25-jdk-noble

# Copy Files
WORKDIR /usr/src/app
COPY src src
COPY .mvn .mvn
COPY pom.xml mvnw ./

# Install
RUN sed -i 's/\r$//' mvnw
RUN chmod +x mvnw
RUN ./mvnw -Dmaven.test.skip=true package

# Docker Run Command
EXPOSE 8082
CMD ["java","-jar","/usr/src/app/target/consumer-0.0.1-SNAPSHOT.jar"]