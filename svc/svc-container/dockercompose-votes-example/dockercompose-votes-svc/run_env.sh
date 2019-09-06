#!/bin/bash

CMD="$@"
OPTIONS=""

#ref: https://raw.githubusercontent.com/heroku/heroku-buildpack-jvm-common/main/opt/jdbc.sh
if [[ -n "${DATABASE_URL:-}" ]]; then
  pattern="^postgres://(.+):(.+)@(.+)$"
  if [[ "${DATABASE_URL}" =~ $pattern ]]; then
    JDBC_DATABASE_USERNAME="${BASH_REMATCH[1]}"
    JDBC_DATABASE_PASSWORD="${BASH_REMATCH[2]}"
    JDBC_DATABASE_URL="jdbc:postgresql://${BASH_REMATCH[3]}"

    OPTIONS="${OPTIONS} --spring.datasource.url=${JDBC_DATABASE_URL} "
    OPTIONS="${OPTIONS} --spring.datasource.username=${JDBC_DATABASE_USERNAME}"
    OPTIONS="${OPTIONS} --spring.datasource.password=${JDBC_DATABASE_PASSWORD}"
  else
    OPTIONS="${OPTIONS} --no.match=${DATABASE_URL}"
  fi
fi

if [[ -n "${MONGODB_URI:-}" ]]; then
  OPTIONS="${OPTIONS} --spring.data.mongodb.uri=${MONGODB_URI}"
fi

if [[ -n "${PORT:-}" ]]; then
  OPTIONS="${OPTIONS} --server.port=${PORT}"
fi

exec $CMD ${OPTIONS}
