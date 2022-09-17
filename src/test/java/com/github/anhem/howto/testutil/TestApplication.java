package com.github.anhem.howto.testutil;

import com.github.anhem.howto.controller.api.MessageDTO;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class TestApplication {

    @Autowired
    protected TestRestTemplate testRestTemplate;

    protected ResponseEntity<MessageDTO> deleteForEntity(String url) {
        return testRestTemplate.exchange(url, HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {
        });
    }

}
