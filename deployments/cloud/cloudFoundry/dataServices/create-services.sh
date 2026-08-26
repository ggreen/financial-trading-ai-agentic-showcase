You may view ops man at:
https://ndc.kuhn-labs.com

user: kl_viewer
password: mooQuo9eev3aixaroohaeng4iu5baig4


cf create-service p.rabbitmq on-demand-plan rabbitmq -c '{"plugins": {"rabbitmq_stream": true}}'


cf create-service p.redis on-demand-cache  valkey

cf create-service postgres on-demand-postgres-db  postgres
cf create-service p-cloudcache extra-small gemfire