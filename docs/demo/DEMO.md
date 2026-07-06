

Task

```properties
stock-daily-price-batch --input.file.path="file:///Users/Projects/solutions/AI-ML/dev/financial-trading-ai-agentic-showcase/applications/batch/stock-daily-price-batch/src/main/resources/csv/acme_stock_prices.csv"
```


```text
agent-flow=sentiment-news-agent  --server.port=9001 | research-trader-agent --server.port=9002 | portfolio-agent --server.port=9003
ai-sql=portfolio-sql-analytics-mcp-server --server.port=9077 || stock-price --server.port=9999
```



TICKER=ACME-CAR

NEWS=Recently we have heard news of stock manipulation by this company.


Add Context

```properties
context=stock manipulation is BEARISH
sumamry=Stock Manipulation: Corporation Leadership Trust Concerns
```

```properties
name=ACME-HOSPITAL
NEWS=Doctors are making up phony results that is a trust concern 
```

```properties
name=ACME-TAX
NEWS=Tax preparer are providing fake and false results
```

Add Context 

```properties
NEWS=fake false results
summary=Fraud Manipulation: Corporation Trust Concerns
```

```properties
deployer.research-trader-agent.local.javaOpts=-Dspring.cloud.stream.rabbit.default.consumer.containerType=stream -Dspring.cloud.stream.rabbit.bindings.input.consumer.containerType=stream -Dspring.cloud.stream.rabbit.bindings.output.consumer.containerType=stream -Dspring.cloud.stream.rabbit.bindings.output.producer.producerType=STREAM_SYNC
deployer.portfolio-agent.local.javaOpts=-Dspring.cloud.stream.rabbit.default.consumer.containerType=stream -Dspring.cloud.stream.rabbit.bindings.input.consumer.containerType=stream -Dspring.cloud.stream.rabbit.bindings.output.consumer.containerType=stream
deployer.sentiment-news-agent.local.javaOpts=-Dspring.cloud.stream.rabbit.default.consumer.containerType=stream -Dspring.cloud.stream.rabbit.bindings.input.consumer.containerType=stream  -Dspring.cloud.stream.rabbit.bindings.output.producer.producerType=STREAM_SYNC



```