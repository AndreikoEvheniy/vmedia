package com.media.vmedia.controller;

import com.media.vmedia.service.ProxyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProxyControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ProxyController proxyController;

    @Autowired
    private ProxyService proxyService;

    @Test
    public void contextLoads() {
        assertThat(proxyController).isNotNull();
        assertThat(proxyService).isNotNull();
    }

    @Test
    public void testProxyRequestWithRealServer() {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject("http://localhost:" + port + "/solutions", String.class);

        assertThat(response).contains("Spring™ Boot"); // Модифицированный контент с символом ™
    }

    @Test
    public void testProxyRequestNonExistingPath() {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject("http://localhost:" + port + "/non-existing-path", String.class);

        assertThat(response).contains("Error: Unexpected error occurred"); // Ошибка для несуществующего пути
    }
}