package io.cloudNativeData.research.trader.agent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class ResearchTraderAgentApp {

    public static void main(String[] args) {
        log.info("App start: properties:{},  args: {}\n env: {}",System.getProperties(),
                args,System.getenv());

        SpringApplication.run(ResearchTraderAgentApp.class, args);
    }
}
