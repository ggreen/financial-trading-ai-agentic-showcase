export GMC_HOME=$GEMFIRE_HOME/../gideon-console/

java --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED  --add-opens java.base/java.io=ALL-UNNAMED -jar -jar $GMC_HOME/gemfire-management-console-1.4.0.jar --server.port=7077

#podman run -it -p 7077:8080 --rm --name gmc-console  --network=gemfire gemfire/gemfire-management-console:1.4
