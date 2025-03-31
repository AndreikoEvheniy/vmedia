package com.media.vmedia.service;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProxyServiceTest {

    private static final String HEADER_NAME = "HeaderName";
    private static final String HEADERS_VALUE = "HeaderValue";
    private static final String EMPTY_STRING = "";

    @Mock
    private WebClient webClient;

    @InjectMocks
    private ProxyService myServiceUnderTest; // Replace with your actual class name

    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

//        when(webClient.get()).thenReturn(requestHeadersSpec);
//        when(requestHeadersSpec.uri(ArgumentMatchers.anyString())).thenReturn(requestHeadersSpec);
//        when(requestHeadersSpec.header(HEADER_NAME, HEADERS_VALUE)).thenReturn(requestHeadersSpec);
//        when(requestHeadersSpec.accept(MediaType.TEXT_HTML)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void testFetchAndModifyContent_SuccessfulResponse() {
        // Arrange
        String relativePath = "/test/path";
        String inputContent = "Hello world! Simple words™ here.";
        String expectedResult = "Hello™ world! Simple™ words™ here.";

        DataBuffer dataBuffer = createDataBuffer(inputContent);
        when(responseSpec.bodyToFlux(DataBuffer.class)).thenReturn(Flux.just(dataBuffer));

        // Act
        String result = myServiceUnderTest.fetchAndModifyContent(relativePath);

        // Assert
        assertEquals(expectedResult, result);
        verify(responseSpec).bodyToFlux(DataBuffer.class);
    }

    @Test
    void testFetchAndModifyContent_EmptyContent() {
        // Arrange
        String relativePath = "/empty/content";
        when(responseSpec.bodyToFlux(DataBuffer.class)).thenReturn(Flux.empty());

        // Act
        String result = myServiceUnderTest.fetchAndModifyContent(relativePath);

        // Assert
        assertEquals("No content received or content was empty.", result);
        verify(responseSpec).bodyToFlux(DataBuffer.class);
    }

    @Test
    void testFetchAndModifyContent_WebClientResponseException() {
        // Arrange
        String relativePath = "/test/error-path";
        WebClientResponseException exception = WebClientResponseException.create(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                null,
                null,
                null
        );
        when(responseSpec.bodyToFlux(DataBuffer.class)).thenThrow(exception);

        // Act
        String result = myServiceUnderTest.fetchAndModifyContent(relativePath);

        // Assert
        assertEquals("Error: Unexpected error occurred for path /test/error-path. HTTP Status: 400 BAD_REQUEST", result);
        verify(responseSpec).bodyToFlux(DataBuffer.class);
    }

    @Test
    void testFetchAndModifyContent_GeneralException() {
        // Arrange
        String relativePath = "/test/general-error";
        when(responseSpec.bodyToFlux(DataBuffer.class)).thenThrow(new RuntimeException("General error"));

        // Act
        String result = myServiceUnderTest.fetchAndModifyContent(relativePath);

        // Assert
        assertEquals("Error: Unexpected error occurred for path /test/general-error. Message: General error", result);
        verify(responseSpec).bodyToFlux(DataBuffer.class);
    }

    // Helper method to create mock DataBuffer
    private DataBuffer createDataBuffer(String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        DefaultDataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
        DataBuffer dataBuffer = dataBufferFactory.allocateBuffer(bytes.length);
        dataBuffer.write(bytes);
        return dataBuffer;
    }
}