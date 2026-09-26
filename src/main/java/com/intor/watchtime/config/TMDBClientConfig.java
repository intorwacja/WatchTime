package com.intor.watchtime.config;

import com.intor.watchtime.exception.ExternalMediaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class TMDBClientConfig {

    @Value("${media.api-key}")
    private String apiKey;

    @Value("${media.base-url}")
    private String baseUrl;

    @Bean
    public TMDBClient tmdbClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    request.getHeaders().setBearerAuth(apiKey);
                    return execution.execute(request, body);
                })
                .defaultStatusHandler(HttpStatusCode::isError, (_, res) -> {
                    throw new ExternalMediaException("TMDB error: " + res.getStatusCode());
                })
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(adapter)
                .build();

        return factory.createClient(TMDBClient.class);
    }
}
