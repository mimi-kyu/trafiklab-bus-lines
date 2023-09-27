package dev.mimi.trafiklabbuslines.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class TrafiklabConfig {

    private String trafiklabUrl;

    @Bean
    public WebClient trafiklabWebClient(
            WebClient.Builder webClientBuilder,
            @Value("${application.trafiklab.endpoint.url}") String trafiklabUrl) {
        return webClientBuilder
                .baseUrl(trafiklabUrl)
                .defaultHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate")
                .build();
    }
}
