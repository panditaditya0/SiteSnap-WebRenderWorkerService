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

java -jar /app/app.jar &
SPRING_BOOT_PID=$!
terminate() {
  echo "Terminating processes..."
  kill -TERM "$CHROMEDRIVER_PID" "$SPRING_BOOT_PID"
  exit 0
}
trap terminate SIGINT SIGTERM
wait -n
EXIT_STATUS=$?
kill -TERM "$CHROMEDRIVER_PID" "$SPRING_BOOT_PID"
exit $EXIT_STATUS
