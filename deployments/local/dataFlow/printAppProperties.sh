echo off
echo "------   Copy The following App properties-----------"
echo source.sentiment-news-agent=file://$PWD/applications/sentiment-news-agent/build/libs/sentiment-news-agent-0.0.1-SNAPSHOT.jar
echo source.sentiment-news-agent.bootVersion=3

echo processor.research-trader-agent=file://$PWD/applications/research-trader-agent/build/libs/research-trader-agent-0.0.1-SNAPSHOT.jar
echo processor.research-trader-agent.bootVersion=3

echo sink.portfolio-agent=file://$PWD/applications/portfolio-agent/build/libs/portfolio-agent-0.0.1-SNAPSHOT.jar
echo sink.portfolio-agent.bootVersion=3


echo app.stock-price=file://$PWD/applications/api/stock-price-api/build/libs/stock-price-api-0.0.1-SNAPSHOT.jar
echo app.stock-price.bootVersion=3

echo app.portfolio-sql-analytics-mcp-server=file://$PWD/applications/mcp/portfolio-sql-analytics-mcp-server/build/libs/portfolio-sql-analytics-mcp-server-0.0.1-SNAPSHOT.jar
echo app.portfolio-sql-analytics-mcp-server.bootVersion=3

echo task.stock-daily-price-batch=file://$PWD/applications/batch/stock-daily-price-batch/build/libs/stock-daily-price-batch-0.0.1-SNAPSHOT.jar
echo task.stock-daily-price-batch.bootVersion=3