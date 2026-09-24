

Create StockDailyPrice


```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create region --name=StockDailyPrice --type=PARTITION_PERSISTENT"
```


```properties

processor.research-trader-agent=maven://com.github.ggreen:research-trader-agent:0.0.2
```