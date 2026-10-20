package com.example.inventory.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class LinkPreviewService {

    private final RestTemplate restTemplate;

    public LinkPreviewService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> preview(String url) {
        String body = restTemplate.getForObject(url, String.class);
        String snippet = body == null ? "" : body.substring(0, Math.min(body.length(), 200));
        return Map.of("url", url, "length", body == null ? 0 : body.length(), "snippet", snippet);
    }
}
