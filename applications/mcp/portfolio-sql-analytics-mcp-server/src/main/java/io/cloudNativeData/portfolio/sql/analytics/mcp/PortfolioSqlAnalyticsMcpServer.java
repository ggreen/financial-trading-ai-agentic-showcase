package io.cloudNativeData.portfolio.sql.analytics.mcp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class PortfolioSqlAnalyticsMcpServer {

	public static void main(String[] args) {

		log.info("App start: properties:{},  args: {}\n env: {}",System.getProperties(),
				args,System.getenv());

		SpringApplication.run(PortfolioSqlAnalyticsMcpServer.class, args);
	}

}
