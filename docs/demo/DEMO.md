

- http://demo.cloudNativeData.io:7077
- http://demo.cloudNativeData.io:15672
- http://demo.cloudNativeData.io:9001
- http://demo.cloudNativeData.io:9002
- http://demo.cloudNativeData.io:9003


"ACME_WELL", "ACME_SPORT","ACME_INS","ACME_PHARMA","ACME_TELCO"

Task



IA

- What are the highest 3  stocks by price
- What is the lowest 2 stock by price
- What is that top 3 stocker based on a BUY tradeProposal

```properties
name=stock-daily-price-batch
```

```properties
stock-daily-price-batch --input.file.path="file:///Users/Projects/solutions/AI-ML/dev/financial-trading-ai-agentic-showcase/applications/batch/stock-daily-price-batch/src/main/resources/csv/acme_stock_prices.csv"
```


```text
agent-flow=sentiment-news-agent  --server.port=9001 | research-trader-agent --server.port=9002 | portfolio-agent --server.port=9003
ai-sql=portfolio-sql-analytics-mcp-server --server.port=9077 || stock-price --server.port=9999
```



## Ingest News

```properties
TICKER=ACME-CAR
NEWS=Recently we have heard news of stock manipulation by this company.

```


Add Context

```properties
context=stock manipulation is BEARISH
summary=Stock Manipulation: Corporation Leadership Trust Concerns
```

```properties
name=ACME-HOSPITAL
NEWS=Doctors are making up phony results that are stock trust concerns because of manipulate
```

```properties
name=ACME-TAX
NEWS=Tax preparers are providing fake and or often false results
```

Add Context 

```properties
NEWS=ACME
summary=Fraud Manipulation: Corporation Trust Concerns
```

```properties
deployer.research-trader-agent.local.javaOpts=-Dspring.cloud.stream.rabbit.default.consumer.containerType=stream -Dspring.cloud.stream.rabbit.bindings.input.consumer.containerType=stream -Dspring.cloud.stream.rabbit.bindings.output.consumer.containerType=stream -Dspring.cloud.stream.rabbit.bindings.output.producer.producerType=STREAM_SYNC
deployer.portfolio-agent.local.javaOpts=-Dspring.cloud.stream.rabbit.default.consumer.containerType=stream -Dspring.cloud.stream.rabbit.bindings.input.consumer.containerType=stream -Dspring.cloud.stream.rabbit.bindings.output.consumer.containerType=stream
deployer.sentiment-news-agent.local.javaOpts=-Dspring.cloud.stream.rabbit.default.consumer.containerType=stream -Dspring.cloud.stream.rabbit.bindings.input.consumer.containerType=stream  -Dspring.cloud.stream.rabbit.bindings.output.producer.producerType=STREAM_SYNC



```