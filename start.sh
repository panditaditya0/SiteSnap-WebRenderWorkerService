#!/bin/bash

set -euo pipefail

/usr/local/bin/chromedriver \
    --port=9200 \
    --whitelisted-ips= \
    --verbose \
    --log-path=/var/log/chromedriver.log \
    --disable-dev-shm-usage \
    --no-sandbox \
    --disable-gpu \
    --headless &

CHROMEDRIVER_PID=$!


# Start the Spring Boot application
java -jar /app/app.jar &

# Capture Spring Boot's PID
SPRING_BOOT_PID=$!

# Function to handle termination signals
terminate() {
  echo "Terminating processes..."
  kill -TERM "$CHROMEDRIVER_PID" "$SPRING_BOOT_PID"
  exit 0
}

# Trap termination signals to gracefully shut down
trap terminate SIGINT SIGTERM

# Wait for any process to exit
wait -n

# Capture exit status
EXIT_STATUS=$?

# Terminate all background processes
kill -TERM "$CHROMEDRIVER_PID" "$SPRING_BOOT_PID"

# Exit with the status of the first process that exited
exit $EXIT_STATUS
