#!/bin/bash

# Start Selenium Hub in the background
java -jar /app/selenium-server.jar hub &

# Wait for Selenium Hub to start
sleep 15

# Start Selenium Node (Chrome) and register it with the Hub
java -jar /app/selenium-server.jar node \
  --hub http://localhost:4444 \
  --detect-drivers true \
  --register-cycle 0 &

# Wait for the Node to register
sleep 15

# Start the Spring Boot application
java -jar /app/app.jar
