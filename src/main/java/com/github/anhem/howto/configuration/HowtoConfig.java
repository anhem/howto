package com.github.anhem.howto.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties("howto")
public class HowtoConfig {

    @NotNull
    @Size(min = 10)
    private String jwtSecret;
    @NotNull
    @Valid
    private UrlHausConfig urlHaus;

    @Data
    public static class UrlHausConfig {
        @NotNull
        @Size(min = 1)
        private String baseUrl;
        @NotNull
        @Min(1)
        @Max(10)
        private int maxAllowedUrls;
    }
}
