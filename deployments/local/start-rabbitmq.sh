podman run --rm -it \
  --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  -p 5552:5552 \
  -e RABBITMQ_SERVER_ADDITIONAL_ERL_ARGS="-rabbitmq_stream advertised_host \"localhost\"" \
  -e RABBITMQ_PLUGINS_WITH_TO_START="rabbitmq_stream rabbitmq_management" \
  rabbitmq:4.3-management