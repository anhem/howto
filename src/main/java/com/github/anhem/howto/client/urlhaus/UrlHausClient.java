package com.github.anhem.howto.client.urlhaus;

import com.github.anhem.howto.client.urlhaus.model.UrlCheckResponse;
import com.github.anhem.howto.configuration.HowtoConfig;
import com.github.anhem.howto.exception.ValidationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@Component
public class UrlHausClient {

    static final String TOO_MANY_URLS = "Too many (%d) urls provided. Maximum allowed is %d";
    private final HowtoConfig.UrlHausConfig urlHausConfig;
    private final RestTemplate urlHausRestTemplate;

    public UrlHausClient(HowtoConfig howtoConfig, RestTemplate urlHausRestTemplate) {
        this.urlHausConfig = howtoConfig.getUrlHaus();
        this.urlHausRestTemplate = urlHausRestTemplate;
    }

    public boolean checkForMaliciousUrls(Set<String> urls) {
        if (urls.size() > urlHausConfig.getMaxAllowedUrls()) {
            throw new ValidationException(String.format(TOO_MANY_URLS, urls.size(), urlHausConfig.getMaxAllowedUrls()));
        }
        return urls.stream().anyMatch(this::checkForMaliciousUrl);
    }

    private boolean checkForMaliciousUrl(String url) {
        HttpEntity<MultiValueMap<String, String>> httpEntity = createRequest(url);
        UrlCheckResponse urlCheckResponse = urlHausRestTemplate.postForObject(String.format("%s/%s", urlHausConfig.getBaseUrl(), "/v1/url/"), httpEntity, UrlCheckResponse.class);

        return isUrlMalicious(urlCheckResponse);
    }

    private static HttpEntity<MultiValueMap<String, String>> createRequest(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("url", url);

        return new HttpEntity<>(map, headers);
    }

    private static boolean isUrlMalicious(UrlCheckResponse urlCheckResponse) {
        if (urlCheckResponse == null) {
            return true;
        }
        return "online".equals(urlCheckResponse.getUrlStatus());
    }

}
