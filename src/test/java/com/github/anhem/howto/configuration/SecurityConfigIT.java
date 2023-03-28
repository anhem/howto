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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.fail;

@Slf4j
class SecurityConfigIT extends TestApplication {

    private static final List<String> GET_URL_WHITELIST = List.of("/swagger-ui.html", "/v3/api-docs", "/v3/api-docs/swagger-config", "/api/hello-world");
    private static final List<String> PATCH_URL_WHITELIST = List.of();
    private static final List<String> POST_URL_WHITELIST = List.of("/api/auth/authenticate");
    private static final List<String> PUT_URL_WHITELIST = List.of();
    private static final List<String> DELETE_URL_WHITELIST = List.of();
    public static final String VALIDATION_ERROR_MESSAGE = "%s on %s does not return %s. Either this url should be protected or it should be added to %s";
    public static final String FAILED_VALIDATION_ERROR_MESSAGE = "%s on %s failed to validate: %s";

    @Autowired
    public RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Test
    void endpointsShouldRespondWithForbiddenWhenNotLoggedIn() {
        List<String> failedEndpoints = new ArrayList<>();
        requestMappingHandlerMapping.getHandlerMethods().forEach((requestMappingInfo, handlerMethod) -> {
            Set<PathPattern> patterns = requestMappingInfo.getPathPatternsCondition().getPatterns();
            Set<RequestMethod> methods = requestMappingInfo.getMethodsCondition().getMethods();
            patterns.forEach(pathPattern -> methods.forEach(assertEndpointForbidden(pathPattern, failedEndpoints)));
        });
        if (!failedEndpoints.isEmpty()) {
            fail(failedEndpoints.stream().collect(Collectors.joining(System.lineSeparator())));
        }
    }

    private Consumer<RequestMethod> assertEndpointForbidden(PathPattern pathPattern, List<String> failedEndpoints) {
        return requestMethod -> {
            try {
                switch (requestMethod.name()) {
                    case "GET" -> assertGetForbidden(pathPattern, failedEndpoints);
                    case "PATCH" -> assertPatchForbidden(pathPattern, failedEndpoints);
                    case "POST" -> assertPostForbidden(pathPattern, failedEndpoints);
                    case "PUT" -> assertPutForbidden(pathPattern, failedEndpoints);
                    case "DELETE" -> assertDeleteForbidden(pathPattern, failedEndpoints);
                    default -> fail(String.format("%s not implemented", requestMethod.name()));
                }
            } catch (Exception e) {
                String failed = String.format(FAILED_VALIDATION_ERROR_MESSAGE, requestMethod.name(), pathPattern.getPatternString(), e.getMessage());
                failedEndpoints.add(failed);
                log.error(failed, e);
            }
        };
    }

    private void assertGetForbidden(PathPattern pathPattern, List<String> failedEndpoints) {
        String url = pathPattern.getPatternString();
        if (!GET_URL_WHITELIST.contains(url)) {
            if (testRestTemplate.getForEntity(cleanUrl(url), Object.class).getStatusCode() != HttpStatus.FORBIDDEN) {
                failedEndpoints.add(String.format(VALIDATION_ERROR_MESSAGE, "GET", url, HttpStatus.FORBIDDEN, "GET_URL_WHITELIST"));
            }
        } else {
            log.info("GET on {} is whitelisted, skipping", url);
        }
    }

    private void assertPatchForbidden(PathPattern pathPattern, List<String> failedEndpoints) {
        String url = pathPattern.getPatternString();
        if (!PATCH_URL_WHITELIST.contains(url)) {
            if (testRestTemplate.exchange(cleanUrl(url), HttpMethod.PATCH, null, Object.class).getStatusCode() != HttpStatus.FORBIDDEN) {
                failedEndpoints.add(String.format(VALIDATION_ERROR_MESSAGE, "PATCH", url, HttpStatus.FORBIDDEN, "PATCH_URL_WHITELIST"));
            }
        } else {
            log.info("PATCH on {} is whitelisted, skipping", url);
        }
    }

    private void assertPostForbidden(PathPattern pathPattern, List<String> failedEndpoints) {
        String url = pathPattern.getPatternString();
        if (!POST_URL_WHITELIST.contains(url)) {
            if (testRestTemplate.postForEntity(cleanUrl(url), MessageDTO.OK, Object.class).getStatusCode() != HttpStatus.FORBIDDEN) {
                failedEndpoints.add(String.format(VALIDATION_ERROR_MESSAGE, "POST", url, HttpStatus.FORBIDDEN, "POST_URL_WHITELIST"));
            }
        } else {
            log.info("POST on {} is whitelisted, skipping", url);
        }
    }

    private void assertPutForbidden(PathPattern pathPattern, List<String> failedEndpoints) {
        String url = pathPattern.getPatternString();
        if (!PUT_URL_WHITELIST.contains(url)) {
            if (testRestTemplate.exchange(cleanUrl(url), HttpMethod.PUT, null, Object.class).getStatusCode() != HttpStatus.FORBIDDEN) {
                failedEndpoints.add(String.format(VALIDATION_ERROR_MESSAGE, "PUT", url, HttpStatus.FORBIDDEN, "PUT_URL_WHITELIST"));
            }
        } else {
            log.info("PUT on {} is whitelisted, skipping", url);
        }
    }

    private void assertDeleteForbidden(PathPattern pathPattern, List<String> failedEndpoints) {
        String url = pathPattern.getPatternString();
        if (!DELETE_URL_WHITELIST.contains(url)) {
            if (testRestTemplate.exchange(cleanUrl(url), HttpMethod.DELETE, null, Object.class).getStatusCode() != HttpStatus.FORBIDDEN) {
                failedEndpoints.add(String.format(VALIDATION_ERROR_MESSAGE, "DELETE", url, HttpStatus.FORBIDDEN, "DELETE_URL_WHITELIST"));
            }
        } else {
            log.info("DELETE on {} is whitelisted, skipping", url);
        }
    }

    private String cleanUrl(String url) {
        return url
                .replace("{", "")
                .replace("}", "");
    }

}