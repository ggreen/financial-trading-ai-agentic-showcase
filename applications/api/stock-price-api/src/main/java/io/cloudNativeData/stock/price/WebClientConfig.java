package io.cloudNativeData.stock.price;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.util.Objects;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient.Builder webClientBuilder() {
        try {
            // Create an SSL Context that accepts any certificate unconditionally
            SslContext sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();

            // Attach this insecure SSL context to Netty's HttpClient
            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

            // Return the custom builder to your application
            return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient));

        } catch (SSLException e) {
            throw new RuntimeException("Failed to initialize insecure WebClient", e);
        }
    }


    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
                Objects.requireNonNull(exchange.getRequest().getRemoteAddress())
                        .getAddress()
                        .getHostAddress()
        );
    }
}
