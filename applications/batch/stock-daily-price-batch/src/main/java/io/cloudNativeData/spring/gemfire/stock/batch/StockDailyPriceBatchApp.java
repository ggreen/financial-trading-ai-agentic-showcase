package io.cloudNativeData.spring.gemfire.stock.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockDailyPriceBatchApp {

	public static void main(String[] args) {
		SpringApplication.run(StockDailyPriceBatchApp.class, args);
	}

}
