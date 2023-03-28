package com.github.anhem.howto.configuration;

import com.github.anhem.howto.controller.model.MessageDTO;
import com.github.anhem.howto.testutil.TestApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@Slf4j
class SecurityConfigIT extends TestApplication {

    private static final List<String> GET_URL_WHITELIST = List.of("/v3/api-docs", "/v3/api-docs/swagger-config", "/api/hello-world");
    private static final List<String> POST_URL_WHITELIST = List.of("/api/auth/authenticate");
    private static final List<String> PUT_URL_WHITELIST = List.of();
    private static final List<String> DELETE_URL_WHITELIST = List.of();
    public static final String VALIDATION_ERROR_MESSAGE = "%s on %s does not return %s. Either this url should be protected or it should be added to %s";

    @Autowired
    public RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Test
    void endpointsShouldRespondWithForbiddenWhenNotLoggedIn() {
        requestMappingHandlerMapping.getHandlerMethods().forEach((requestMappingInfo, handlerMethod) -> {
            Set<PathPattern> patterns = requestMappingInfo.getPathPatternsCondition().getPatterns();
            Set<RequestMethod> methods = requestMappingInfo.getMethodsCondition().getMethods();
            patterns.forEach(pathPattern -> methods.forEach(assertEndpointForbidden(pathPattern)));
        });
    }

    private Consumer<RequestMethod> assertEndpointForbidden(PathPattern pathPattern) {
        return requestMethod -> {
            switch (requestMethod.name()) {
                case "GET" -> assertGetForbidden(pathPattern);
                case "POST" -> assertPostForbidden(pathPattern);
                case "PUT" -> assertPutForbidden(pathPattern);
                case "DELETE" -> assertDeleteForbidden(pathPattern);
                default -> fail(String.format("%s not implemented", requestMethod.name()));
            }
        };
    }

    private void assertGetForbidden(PathPattern pathPattern) {
        String url = pathPattern.getPatternString();
        if (!GET_URL_WHITELIST.contains(url)) {
            assertThat(testRestTemplate.getForEntity(cleanUrl(url), Object.class).getStatusCode())
                    .as(String.format(VALIDATION_ERROR_MESSAGE, "GET", url, HttpStatus.FORBIDDEN, "GET_URL_WHITELIST"))
                    .isEqualTo(HttpStatus.FORBIDDEN);
        } else {
            log.info("GET on {} is whitelisted, skipping", url);
        }
    }

    private void assertPostForbidden(PathPattern pathPattern) {
        String url = pathPattern.getPatternString();
        if (!POST_URL_WHITELIST.contains(url)) {
            assertThat(testRestTemplate.postForEntity(cleanUrl(url), MessageDTO.OK, Object.class).getStatusCode())
                    .as(String.format(VALIDATION_ERROR_MESSAGE, "POST", url, HttpStatus.FORBIDDEN, "POST_URL_WHITELIST"))
                    .isEqualTo(HttpStatus.FORBIDDEN);
        } else {
            log.info("POST on {} is whitelisted, skipping", url);
        }
    }

    private void assertPutForbidden(PathPattern pathPattern) {
        String url = pathPattern.getPatternString();
        if (!PUT_URL_WHITELIST.contains(url)) {
            assertThat(testRestTemplate.exchange(cleanUrl(url), HttpMethod.PUT, null, Object.class).getStatusCode())
                    .as(String.format(VALIDATION_ERROR_MESSAGE, "PUT", url, HttpStatus.FORBIDDEN, "PUT_URL_WHITELIST"))
                    .isEqualTo(HttpStatus.FORBIDDEN);
        } else {
            log.info("PUT on {} is whitelisted, skipping", url);
        }
    }

    private void assertDeleteForbidden(PathPattern pathPattern) {
        String url = pathPattern.getPatternString();
        if (!DELETE_URL_WHITELIST.contains(url)) {
            assertThat(testRestTemplate.exchange(cleanUrl(url), HttpMethod.DELETE, null, Object.class).getStatusCode())
                    .as(String.format(VALIDATION_ERROR_MESSAGE, "DELETE", url, HttpStatus.FORBIDDEN, "DELETE_URL_WHITELIST"))
                    .isEqualTo(HttpStatus.FORBIDDEN);
        } else {
            log.info("DELETE on {} is whitelisted, skipping", url);
        }
    }

    private static String cleanUrl(String url) {
        return url
                .replace("{", "")
                .replace("}", "");
    }

}