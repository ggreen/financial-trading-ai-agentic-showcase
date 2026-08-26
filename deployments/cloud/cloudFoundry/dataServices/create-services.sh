cf create-service p.rabbitmq on-demand-plan rabbitmq -c '{"plugins": {"rabbitmq_stream": true}}'


cf create-service p.redis on-demand-cache  valkey

cf create-service postgres on-demand-postgres-db  postgres
cf create-service p-cloudcache extra-small gemfire