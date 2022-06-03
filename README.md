# Deploy Spring Boot Application in AWS API Gateway with Lambda Authorizer

### Build
###### Build application and create local image
```
mvn clean package dockerfile:build
```

###### Build application and push image to remote repository
```
mvn clean package dockerfile:push
```

### Run
###### Local
Metadata Service
Refer run config in [this](.run) folder.

###### docker compose
```
docker-compose up -d

docker-compose ps

docker-compose down

docker-compose logs -f --tail="all"
docker-compose logs -f --tail="100"

docker-compose logs -f --tail="all" metadata
```

http://localhost:8080/api/v1/metadata/h2-console
