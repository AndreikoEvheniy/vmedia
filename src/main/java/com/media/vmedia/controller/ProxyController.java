package com.media.vmedia.controller;

import com.media.vmedia.service.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ProxyController {

    //The part of the path after the “/”
    private static final int PART_OF_THE_PATH = 1;

    private final ProxyService proxyService;

    @GetMapping("/**")
    public ResponseEntity<String> proxyRequest(HttpServletRequest request) {
        String relativePath = request.getRequestURI().substring(PART_OF_THE_PATH);
        log.info("Received request for path: {}", relativePath);

        if (relativePath.equals("favicon.ico")) {
            log.warn("Favicon.ico requested, returning 404.");
            return ResponseEntity.notFound().build();
        }

        String modifiedContent = proxyService.fetchAndModifyContent(relativePath);
        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.TEXT_HTML).body(modifiedContent);
    }
}
