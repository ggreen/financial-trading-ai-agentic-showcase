export ROOT_DIR=$PWD
export DATAFLOW_HOME=/Users/devtools/integration/scdf/tanzu

export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postgresml
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=
export SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
export SPRING_APPLICATION_JSON="{\"spring.cloud.stream.binders.rabbitBinder.environment.spring.rabbitmq.username\":\"$TANZU_RABBITMQ_USER\",\"spring.cloud.stream.binders.rabbitBinder.environment.spring.rabbitmq.password\":\"$TANZU_RABBITMQ_PASSWORD\",\"spring.rabbitmq.username\":\"$TANZU_RABBITMQ_USER\",\"spring.rabbitmq.password\":\"$TANZU_RABBITMQ_PASSWORD\",\"spring.cloud.dataflow.applicationProperties.stream.spring.rabbitmq.username\" :\"$TANZU_RABBITMQ_USER\",\"spring.cloud.dataflow.applicationProperties.stream.spring.rabbitmq.password\" :\"$TANZU_RABBITMQ_PASSWORD\",\"spring.datasource.url\" :\"$SPRING_DATASOURCE_URL\",\"spring.datasource.driver-class-name\" :\"org.postgresql.Driver\",\"spring.datasource.username\" :\"$SPRING_DATASOURCE_USERNAME\",\"spring.datasource.password\" :\"$SPRING_DATASOURCE_PASSWORD\",\"API_NINJAS_KEY\" : \"$API_NINJAS_KEY\"}"

mkdir -p runtime/dataflow/logs


export SPRING_CLOUD_DEPLOYER_LOCAL_envVarsToInherit="LC_.*"

java -jar  $DATAFLOW_HOME/scdf-pro-skipper-1.6.13.jar > runtime/dataflow/logs/skipper.log&

java -jar $DATAFLOW_HOME/scdf-pro-server-1.6.13.jar  --server.port=9393  --spring.cloud.dataflow.features.skipper-enabled=true > runtime/dataflow/logs/scdf.log &

