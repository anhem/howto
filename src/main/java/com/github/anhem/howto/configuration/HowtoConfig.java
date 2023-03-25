package com.github.anhem.howto.configuration;

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
}
