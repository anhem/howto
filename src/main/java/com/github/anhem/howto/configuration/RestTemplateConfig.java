package com.github.anhem.howto.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate urlHausRestTemplate(ObjectMapper objectMapper) {
        ObjectMapper snakeCaseObjectMapper = createSnakeCaseObjectMapper(objectMapper);

        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(snakeCaseObjectMapper);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, mappingJackson2HttpMessageConverter);

        return restTemplate;
    }

    public static ObjectMapper createSnakeCaseObjectMapper(ObjectMapper objectMapper) {
        return objectMapper.copy().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }
}
