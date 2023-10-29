FROM maven:3.8.1-openjdk-17-slim AS build

WORKDIR /app

COPY . /app/

CMD ["mvn", "spring-boot:run"]