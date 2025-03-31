package com.media.vmedia.service;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class ProxyService {

    private static final String BASE_URL = "https://spring.io";

    private static final String HEADERS_VALUE = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36";

    private static final String HEADER_NAME = "User-Agent";

    private static final String EMPTY_STRING = "";

    private final WebClient webClient;

    public ProxyService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }

    /**
     * Executes a request to URL, modifies the content, and returns it.
     *
     * @param relativePath client-requested path
     * @return modified HTML content
     */
    public String fetchAndModifyContent(String relativePath) {
        try {
            log.info("Fetching content for path: {}", relativePath);

            Flux<DataBuffer> dataBufferFlux = getDataBufferFlux(relativePath);

            String content = getContent(dataBufferFlux);

            if (Objects.isNull(content) || content.isEmpty()) {
                log.error("Content is empty for path: {}", relativePath);
                return "No content received or content was empty.";
            }

            log.info("Content fetched successfully for path {} with size: {} bytes", relativePath, content.length());
            return modifySixLetterWords(content);
        } catch (WebClientResponseException e) {
            HttpStatusCode status = e.getStatusCode();
            log.error("Server returned an error for path {}. HTTP Status: {}", relativePath, status);
            return "Error: Unexpected error occurred for path " + relativePath + ". HTTP Status: " + status;
        } catch (Exception e) {
            log.error("Unexpected error occurred for path {}: {}", relativePath, e.getMessage(), e);
            return "Error: Unexpected error occurred for path " + relativePath + ". Message: " + e.getMessage();
        }
    }

    /**
     * Get data from Flux<DataBuffer>
     *
     * @param dataBufferFlux data from server
     * @return content
     */
    private String getContent(Flux<DataBuffer> dataBufferFlux) {
        return dataBufferFlux
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .reduce(EMPTY_STRING, String::concat)
                .block();
    }

    /**
     * Get data from server
     *
     * @param relativePath content path
     * @return Flux<DataBuffer> content
     */
    private Flux<DataBuffer> getDataBufferFlux(String relativePath) {
        return webClient.get()
                .uri(relativePath)
                .header(HEADER_NAME, HEADERS_VALUE)
                .accept(MediaType.TEXT_HTML)
                .retrieve()
                .bodyToFlux(DataBuffer.class);
    }

    /**
     * Replaces each six-letter word in the text by adding a “™” character.
     *
     * @param content original text
     * @return modified text
     */
    private String modifySixLetterWords(String content) {
        if (Objects.isNull(content) || content.isEmpty()) {
            return "No content received or content was empty.";
        }
        return content.replaceAll("\\b\\w{6}\\b", "$0™");
    }
}