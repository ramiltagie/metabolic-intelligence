package com.metabolicintelligence.config.openai;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "openai")
@Validated
@Data
public class OpenAiProperties {
    @NotBlank
    private String baseUrl;

    @NotBlank
    private String model;

    @NotBlank
    private String apiKey;
}