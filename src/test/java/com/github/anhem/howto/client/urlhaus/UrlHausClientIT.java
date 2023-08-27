package com.github.anhem.howto.client.urlhaus;

import com.github.anhem.howto.exception.ValidationException;
import com.github.anhem.howto.testutil.TestApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import static com.github.anhem.howto.client.urlhaus.UrlHausClient.TOO_MANY_URLS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class UrlHausClientIT extends TestApplication {

    private static final String URL = "https://local.home.arpa/";
    private static final String URL_ENCODED = "url=https%3A%2F%2Flocal.home.arpa%2F";

    @Value("classpath:/urlhaus/malicious-url-response.json")
    private Resource maliciousUrlResponse;
    @Value("classpath:/urlhaus/ok-url-response.json")
    private Resource okUrlResponse;
    @Autowired
    private RestTemplate urlHausRestTemplate;
    @Autowired
    private UrlHausClient urlHausClient;
    private MockRestServiceServer mockRestServiceServer;
    private URI uri;

    @BeforeEach
    public void setUp() throws URISyntaxException {
        mockRestServiceServer = MockRestServiceServer.createServer(urlHausRestTemplate);
        uri = new URI("http://localhost:8080/v1/url/");
    }

    @Test
    void checkForMaliciousUrlsReturnsFalseWhenUrlIsMalicious() {
        setupUrlHausResponse(maliciousUrlResponse);

        assertThat(urlHausClient.checkForMaliciousUrls(Set.of(URL))).isTrue();
    }

    @Test
    void checkForMaliciousUrlsReturnsTrueWhenUrlIsOk() {
        setupUrlHausResponse(okUrlResponse);

        assertThat(urlHausClient.checkForMaliciousUrls(Set.of(URL))).isFalse();
    }

    @Test
    void exceptionIsThrownWhenMaxAllowedUrlsHasBeenPassed() {
        assertThatThrownBy(() -> urlHausClient.checkForMaliciousUrls(Set.of(URL, "https://home.local/")))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(String.format(TOO_MANY_URLS, 2, 1));
    }

    private void setupUrlHausResponse(Resource response) {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(uri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(URL_ENCODED))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response)
                );
    }
}
