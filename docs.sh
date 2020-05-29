#!/bin/bash

BASEDIR="$( cd "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"

echo "Starting documentation server on http://localhost"
docker run -p 80:8080 -e SWAGGER_JSON=/docs/swagger.json -v $BASEDIR/src/main/resources/openapi.json:/docs/swagger.json swaggerapi/swagger-ui