package com.github.anhem.howto.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    public RestClient.Builder urlHausRestClientBuilder(ObjectMapper objectMapper) {
        ObjectMapper snakeCaseObjectMapper = createSnakeCaseObjectMapper(objectMapper);

        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(snakeCaseObjectMapper);

        return RestClient.builder()
                .messageConverters(converters -> converters.add(0, mappingJackson2HttpMessageConverter));
    }

    @Bean
    public RestClient urlHausRestClient(RestClient.Builder urlHausRestClientBuilder) {
        return urlHausRestClientBuilder.build();
    }

    public static ObjectMapper createSnakeCaseObjectMapper(ObjectMapper objectMapper) {
        return objectMapper.copy()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }
}
