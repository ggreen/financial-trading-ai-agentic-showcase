package io.cloudNativeData.portfolio.agent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class PortfolioAgentApp {

    public static void main(String[] args) {
        log.info("App start: properties:{},  args: {}\n env: {}",System.getProperties(),
                args,System.getenv());

        SpringApplication.run(PortfolioAgentApp.class, args);
    }
}
