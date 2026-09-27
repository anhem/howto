package com.github.anhem.howto.client.urlhaus;

import com.github.anhem.howto.client.urlhaus.model.UrlCheckResponse;
import com.github.anhem.howto.configuration.HowtoConfig;
import com.github.anhem.howto.exception.ValidationException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Set;

@Component
public class UrlHausClient {

    static final String TOO_MANY_URLS = "Too many (%d) urls provided. Maximum allowed is %d";
    private final HowtoConfig.UrlHausConfig urlHausConfig;
    private final RestClient urlHausRestClient;

    public UrlHausClient(HowtoConfig howtoConfig, RestClient urlHausRestClient) {
        this.urlHausConfig = howtoConfig.getUrlHaus();
        this.urlHausRestClient = urlHausRestClient;
    }

    public boolean checkForMaliciousUrls(Set<String> urls) {
        if (urls.size() > urlHausConfig.getMaxAllowedUrls()) {
            throw new ValidationException(String.format(TOO_MANY_URLS, urls.size(), urlHausConfig.getMaxAllowedUrls()));
        }
        return urls.stream().anyMatch(this::checkForMaliciousUrl);
    }

    private boolean checkForMaliciousUrl(String url) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("url", url);

        UrlCheckResponse urlCheckResponse = urlHausRestClient.post()
                .uri(String.format("%s/%s", urlHausConfig.getBaseUrl(), "/v1/url/"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(map)
                .retrieve()
                .body(UrlCheckResponse.class);

        return isUrlMalicious(urlCheckResponse);
    }

    private static boolean isUrlMalicious(UrlCheckResponse urlCheckResponse) {
        if (urlCheckResponse == null) {
            return true;
        }
        return "online".equals(urlCheckResponse.getUrlStatus());
    }

}
