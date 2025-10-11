package ru.practicum.client;

import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;
import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(String serverUrl) {
        this.rest = new RestTemplate();
        rest.setUriTemplateHandler(new DefaultUriBuilderFactory(serverUrl));
    }

    protected ResponseEntity<Object> get(String path, Map<String, Object> parameters) {
        return sendRequest(HttpMethod.GET, path, parameters, null);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return sendRequest(HttpMethod.POST, path, null, body);
    }

    private <T> ResponseEntity<Object> sendRequest(HttpMethod method, String path,
                                                   Map<String, Object> parameters, T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, createHeaders());
        try {
            if (parameters != null) {
                return rest.exchange(path, method, requestEntity, Object.class, parameters);
            }
            return rest.exchange(path, method, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}