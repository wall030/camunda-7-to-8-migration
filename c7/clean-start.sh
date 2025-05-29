#!/bin/bash

echo "Stopping and removing existing containers..."
docker-compose down

echo "Removing volumes (optional)..."
docker-compose down -v

echo "Building the Spring Boot application..."
(cd backend && ./gradlew clean build)

echo "Rebuilding the Spring Boot application Docker image..."
docker-compose build

echo "Starting up the services..."
docker-compose up -d

echo "Services started..."
docker-compose logs -f app