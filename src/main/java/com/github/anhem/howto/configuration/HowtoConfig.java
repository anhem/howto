package com.github.anhem.howto.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Validated
@Configuration
@ConfigurationProperties("howto")
public class HowtoConfig {

    @NotNull
    @Size(min = 10)
    private String jwtSecret;
}
