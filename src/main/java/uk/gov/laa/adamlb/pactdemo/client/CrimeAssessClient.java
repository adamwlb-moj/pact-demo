package uk.gov.laa.adamlb.pactdemo.client;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
final class CrimeAssessClient {

    static final String ERROR_STRING = "An error occurred finding the koala's location";

    private final HttpClient httpClient;
    private final URI baseUrl;

    public String getKoalaLocation(String day) {
        HttpRequest request = buildRequest(day);
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 400 ? ERROR_STRING : response.body();
        } catch (IOException exception) {
            return ERROR_STRING;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return ERROR_STRING;
        }
    }

    private HttpRequest buildRequest(String day) {
        String encodedDay = URLEncoder.encode(day, StandardCharsets.UTF_8);
        return HttpRequest.newBuilder()
                .uri(baseUrl.resolve("/koala/location?day=" + encodedDay))
                .GET()
                .build();
    }
}
