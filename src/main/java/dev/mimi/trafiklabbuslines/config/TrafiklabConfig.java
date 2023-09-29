package dev.mimi.trafiklabbuslines.config;

import dev.mimi.trafiklabbuslines.service.TrafiklabService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class TrafiklabConfig {

    private String trafiklabUrl;
    private static final Logger log = LoggerFactory
            .getLogger(TrafiklabConfig.class);

    @Bean
    public WebClient trafiklabWebClient(
            WebClient.Builder webClientBuilder,
            @Value("${application.trafiklab.endpoint.url}") String trafiklabUrl) {
        log.info("Configuring WebClient");
        return webClientBuilder
                .baseUrl(trafiklabUrl)
                .defaultHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate")
                .build();
    }
}
