package com.media.vmedia.service;


import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class ProxyServiceTest {

    private MockWebServer mockWebServer;
    private ProxyService proxyService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        proxyService = new ProxyService(WebClient.builder().baseUrl(mockWebServer.url("/").toString()));
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testFetchAndModifyContentWithValidResponse() {
        String mockHtmlResponse = "<html><body>Spring Boot makes it easy to create applications.</body></html>";
        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "text/html")
                .setBody(mockHtmlResponse));

        String content = proxyService.fetchAndModifyContent("/solutions/");

        assertThat(content).contains("Your California Privacy Rights™</a></p><p class=\"has-text-grey-light\">Apache™®, Apache™ Tomcat™®, Apache™ Kafka®, Apache™ Cassandra™, and Apache™ Geode™ are trademarks or registered trademarks of the Apache™ Software Foundation in the United™ States™ and/or other countries. Java™");
    }

    @Test
    void testFetchAndModifyContentWithEmptyResponse() {
        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "text/html")
                .setBody(""));

        String content = proxyService.fetchAndModifyContent("/solutions");

        assertThat(content).isEqualTo("No content received or content was empty.");
    }

    @Test
    void testFetchAndModifyContentWithServerError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error"));

        String content = proxyService.fetchAndModifyContent("/solutions");

        assertThat(content).contains("No content received or content was empty.");
    }
}
