#!/bin/bash


echo "Building the Spring Boot application..."
(cd backend && ./gradlew clean build)
echo "Starting Docker Compose services..."
docker-compose up -d --build