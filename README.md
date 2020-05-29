## Local Development
To ease development, docker-compose is used to start a MySQL instance 
and sets up all dev tools within a build image. The dev environment
is setup to automatically build and redeploy the server on code change.

To get started run the following command at the project root

```
docker-compose up -d
```

To debug the code bind to *localhost:5566*

## Production-Like Environment
Not really production but it will only compile the code once unlike the dev environment

```
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

## App
The app itself runs on port 8080

## Endpoint Documentation
The endpoint documentation server can be started by running

```
./docs.sh
```
