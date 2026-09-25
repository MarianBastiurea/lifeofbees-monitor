package com.lifeofbees.monitor.monitoring;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class WebsiteChecker {

    private final HttpClient httpClient;

    public WebsiteChecker() {
        this.httpClient = HttpClient.newHttpClient();
    }


    public WebsiteStatus check(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<Void> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.discarding()
            );

            return new WebsiteStatus(
                    response.statusCode(),
                    response.statusCode() >= 200 && response.statusCode() < 400
            );

        } catch (IOException | InterruptedException e) {
            return new WebsiteStatus(0, false);
        }
    }
}