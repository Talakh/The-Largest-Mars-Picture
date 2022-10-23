package com.bobocode.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

@Service
public class MarsService {
    private final RestTemplate restTemplate;

    @Value("${nasa.api.url}")
    private String nasaUrl;
    @Value("${nasa.api.key}")
    private String nasaKey;

    public MarsService(final RestTemplate restTemplate) {this.restTemplate = restTemplate;}


    public byte[] getLargestPicture(final Integer sol, final String camera) {
        String marsApiUrl = getMarsApiUrl(sol, camera);
        return restTemplate.getForObject(marsApiUrl, JsonNode.class)
                .findValuesAsText("img_src")
                .parallelStream()
                .map(this::getImageUlrAndSize)
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .map(url -> restTemplate.getForObject(url, byte[].class))
                .orElseThrow(() -> new IllegalArgumentException("No pictures found"));

    }

    private Map.Entry<URI, Long> getImageUlrAndSize(String url) {
        var location = restTemplate.headForHeaders(url).getLocation();
        var imgSize = restTemplate.headForHeaders(location).getContentLength();
        return Map.entry(location, imgSize);
    }

    private String getMarsApiUrl(final Integer sol, final String camera) {
        return UriComponentsBuilder.fromUriString(nasaUrl)
                .queryParam("sol", sol)
                .queryParam("api_key", nasaKey)
                .queryParamIfPresent("camera", Optional.ofNullable(camera))
                .toUriString();
    }
}
