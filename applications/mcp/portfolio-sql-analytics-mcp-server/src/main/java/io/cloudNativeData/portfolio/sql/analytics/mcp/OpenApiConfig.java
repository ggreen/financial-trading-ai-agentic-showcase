//package io.cloudNativeData.portfolio.sql.analytics.mcp;
//
//import org.springdoc.core.customizers.OpenApiCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import io.swagger.v3.oas.models.servers.Server;
//
//import java.util.List;
//
//@Configuration
//public class OpenApiConfig {
//
//    @Bean
//    public OpenApiCustomizer customerServerCustomizer() {
//        return openApi -> {
//            // Clears problematic dynamically guessed servers and forces a safe relative path
//            Server localServer = new Server().url("/").description("Default Server");
//            openApi.setServers(List.of(localServer));
//        };
//    }
//}
