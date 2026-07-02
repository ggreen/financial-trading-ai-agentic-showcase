

```text
agent-flow=sentiment-news-agent | research-trader-agent | portfolio-agent
ai-sql=portfolio-sql-analytics-mcp-server || stock-price
```



TICKER=ACME-CAR

NEWS=Recently we have heard news of stock manipulation by this company.


```properties
deployer.sentiment-news-agent.local.javaOpts=-Dspring.cloud.stream.rabbit.default.consumer.containerType=stream -Dspring.cloud.stream.rabbit.bindings.input.consumer.containerType=stream -Dspring.cloud.stream.rabbit.bindings.output.consumer.containerType=stream
deployer.research-trader-agent.local.javaOpts=-Dspring.cloud.stream.rabbit.default.consumer.containerType=stream
deployer.portfolio-agent.local.javaOpts=-Dspring.cloud.stream.rabbit.default.consumer.containerType=stream
```