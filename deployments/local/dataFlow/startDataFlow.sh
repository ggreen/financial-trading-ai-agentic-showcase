export ROOT_DIR=$PWD
export DATAFLOW_HOME=/Users/devtools/integration/scdf/tanzu
export SPRING_APPLICATION_JSON="{\"spring.cloud.stream.binders.rabbitBinder.environment.spring.rabbitmq.username\":\"$TANZU_RABBITMQ_USER\",\"spring.cloud.stream.binders.rabbitBinder.environment.spring.rabbitmq.password\":\"$TANZU_RABBITMQ_PASSWORD\",\"spring.rabbitmq.username\":\"$TANZU_RABBITMQ_USER\",\"spring.rabbitmq.password\":\"$TANZU_RABBITMQ_PASSWORD\",\"spring.cloud.dataflow.applicationProperties.stream.spring.rabbitmq.username\" :\"$TANZU_RABBITMQ_USER\",\"spring.cloud.dataflow.applicationProperties.stream.spring.rabbitmq.password\" :\"$TANZU_RABBITMQ_PASSWORD\",\"spring.datasource.url\" :\"jdbc:postgresql://localhost:5432/postgresml\",\"spring.datasource.driver-class-name\" :\"org.postgresql.Driver\",\"spring.datasource.username\" :\"postgresml\",\"API_NINJAS_KEY\" : \"$API_NINJAS_KEY\"}"

mkdir -p runtime/dataflow/logs

java -jar  $DATAFLOW_HOME/scdf-pro-skipper-1.6.13.jar > runtime/dataflow/logs/skipper.log&

java -jar $DATAFLOW_HOME/scdf-pro-server-1.6.13.jar  --server.port=9393  --spring.cloud.dataflow.features.skipper-enabled=true > runtime/dataflow/logs/scdf.log &

