# Kafka streams, Redis and AWS Lambda example

- Kafka producer to send content of the products file to the products topic
- Kafka consumer to push the messages from the products topic to Redis.
- Add lambda function to use the Kafka Rest API to get the products from the products topic
- Add lambda function to call the Redis and get the products
 
 You can examine the redis by using the https://github.com/qishibo/AnotherRedisDesktopManager
 
## Lambda setup

Go to the lambda dir. first build the node app. Yarn or other node build tool is required.
https://classic.yarnpkg.com/en/docs/install#windows-stable

```
yarn 
yarn run test # May not work, you can still build run the docker as described below
```
run without building (on Windows powershell):

```
docker run --network=kafka-poc-network -e REDIS_HOST=redis -p 9001:9001 --rm -v ${PWD}:/var/task lambci/lambda:nodejs8.10
```
or in REST mode:
```
docker run --network=kafka-poc-network -e REDIS_HOST=redis -e DOCKER_LAMBDA_STAY_OPEN=1 -e DOCKER_LAMBDA_WATCH=1 -p 9001:9001 --rm -t -i -v ${PWD}:/var/task lambci/lambda:nodejs8.10 index.handler
```

You can then call the Lambda using the API. In the prod env we will need to use API Gateway.
This is also why we always return HTTP 200. To change that we need to use API Gateway - https://medium.com/@dikshitkathuria1803/mapping-api-gateway-with-lambda-output-e8ea9e435bbf.
```
POST  http://localhost:9001/2015-03-31/functions/index/invocations
{
  "get": true,
  "key": "Product:533"
}
```
Example response, that should be processed by the Proxy to compose the proper HTTP response:

```
{
    "statusCode": 200,
    "headers": {
        "Access-Control-Allow-Origin": "*"
    },
    "body": "{\"price\":\"€71,02\",\"primaryAsset\":\"http://dummyimage.com/157x238.jpg/5fa2dd/ffffff\",\"shortDescription\":\"drive cross-platform interfaces\",\"productName\":\"Lotus scoparius (Nutt.) Ottley var. brevialatus Ottley\",\"description\":\"Morbi non lectus. Aliquam sit amet diam in magna bibendum imperdiet. Nullam orci pede, venenatis non, sodales sed, tincidunt eu, felis.\",\"productId\":\"1\",\"_class\":\"com.example.consumer.model.ProductData\",\"size\":\"XS\",\"gender\":\"Female\",\"color\":\"Mauv\"}"
}
```
Example call the Lambda with curl
```
curl -d '{"get": true,"key": "Product:533"}' http://localhost:9001/2015-03-31/functions/index/invocations
```

### Lambda with the REST Kafka consumer

Request body should contain
``{
    "consume": true,
    "key" : 1
  }``
After that the Kafka consumer should start, and read the topic until the message with given key is found.
This can take a few minutes..
Also the response size can be very big when joining the topic.

### Build the docker image with lambda - to be fixed

Go to the lambda dir and execute:
```
docker build -t mylambda .
docker run --rm -e AWS_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY -e DOCKER_LAMBDA_STAY_OPEN=1 mylambda 
```

### References

https://github.com/lambci/docker-lambda 
https://github.com/AraanBranco/lambda-redis
https://github.com/NodeRedis/node-redis
To implement the Redis scan - https://stackoverflow.com/questions/42926990/how-to-get-all-keys-and-values-in-redis-in-javascript/43059247

## Description

The main frameworks:

* Spring Boot - to be able to run console/web application easily in the container.
* Spring Kafka - to integrate with Apache Kafka.
* Apache Maven - to build the project, divide the project into modules. Use a Maven plugin to build docker images
* Docker compose - to set up all the containers, configure ports that need to be open
* NodeJS - for Lambda, Java is also available but it seems to heavyweight for the task and the building process seems more complicated

## Docker setup

In order to deploy Confluent Kafka in the machine, this project starts 3 Docker containers (ZooKeeper, Kafka, Kafka-Topics-UI).

* Build the main pom.xml
`mvn clean package -DskipTests`

### 1. Build the docker Images

#### kafka-docker-producer

```
cd ../kafka-docker-producer
mvn clean package docker:build -DskipTests
```

#### kafka-docker-consumer

```
cd ../kafka-consumer
mvn clean package docker:build -DskipTests
```

#### List the Docker images and check the new images by executing:

```
cd ..
docker images
```

### 2. Create the containers

We are using Docker-Compose to start the containers. Go to the root folder where 'docker-compose.yml' is located and run the below command:
```
docker-compose up -d
```
Or to stop or restart:
```
docker-compose stop
docker-compose restart
```

[Optional] You can either open a separate terminal and follow the logs while systems are initializing:
```
docker-compose logs -f
```
[Optional] Or check the starting status:
```
docker-compose ps
```

### Removing the Docker images created:
```
docker images
docker rmi docker.example.com/kafka-docker-consumer:latest
[...]
```
