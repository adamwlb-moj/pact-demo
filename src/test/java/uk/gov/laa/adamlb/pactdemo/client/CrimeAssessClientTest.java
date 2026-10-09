package uk.gov.laa.adamlb.pactdemo.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.laa.adamlb.pactdemo.client.CrimeAssessClient.ERROR_STRING;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CrimeAssessClientTest {

    private static final String KOALA_SERVICE_LOCATION_URL = "/koala/location";

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> response;

    private CrimeAssessClient underTest;

    @BeforeEach
    void setUp() {
        underTest = new CrimeAssessClient(httpClient, URI.create("http://localhost:9090"));
    }

    @Test
    void validHttpRequestReturnsLocation() throws IOException, InterruptedException {
        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        String day = "Monday";
        String location = "Merthyr Tydfil";

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(location);
        when(httpClient.send(any(HttpRequest.class), ArgumentMatchers.<BodyHandler<String>>any()))
                .thenReturn(response);

        assertThat(underTest.getKoalaLocation(day)).isEqualTo(location);

        verify(httpClient).send(requestCaptor.capture(), ArgumentMatchers.<BodyHandler<String>>any());
        HttpRequest request = requestCaptor.getValue();
        assertThat(request.method()).isEqualTo("GET");
        assertThat(request.uri().getHost()).isEqualTo("localhost");
        assertThat(request.uri().getPort()).isEqualTo(9090);
        assertThat(request.uri().getPath()).isEqualTo(KOALA_SERVICE_LOCATION_URL);
        assertThat(request.uri().getQuery()).isEqualTo("day=" + day);
    }

    @Test
    void badRequestReturnsErrorString() throws IOException, InterruptedException {
        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        String day = "Humpday";

        when(response.statusCode()).thenReturn(400);
        when(httpClient.send(any(HttpRequest.class), ArgumentMatchers.<BodyHandler<String>>any()))
                .thenReturn(response);

        assertThat(underTest.getKoalaLocation(day)).isEqualTo(ERROR_STRING);

        verify(httpClient).send(requestCaptor.capture(), ArgumentMatchers.<BodyHandler<String>>any());
        HttpRequest request = requestCaptor.getValue();
        assertThat(request.method()).isEqualTo("GET");
        assertThat(request.uri().getPath()).isEqualTo(KOALA_SERVICE_LOCATION_URL);
        assertThat(request.uri().getQuery()).isEqualTo("day=" + day);
    }

    @Test
    void ioExceptionReturnsErrorString() throws IOException, InterruptedException {
        when(httpClient.send(any(HttpRequest.class), ArgumentMatchers.<BodyHandler<String>>any()))
                .thenThrow(new IOException("Connection failed"));

        assertThat(underTest.getKoalaLocation("Monday")).isEqualTo(ERROR_STRING);
    }

    @Test
    void interruptedExceptionReturnsErrorStringAndRestoresInterrupt() throws IOException, InterruptedException {
        when(httpClient.send(any(HttpRequest.class), ArgumentMatchers.<BodyHandler<String>>any()))
                .thenThrow(new InterruptedException("Request interrupted"));

        assertThat(underTest.getKoalaLocation("Monday")).isEqualTo(ERROR_STRING);
        assertThat(Thread.currentThread().isInterrupted()).isTrue();
    }

    @Test
    void dayIsEncodedInQuery() throws IOException, InterruptedException {
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("Merthyr Tydfil");
        when(httpClient.send(any(HttpRequest.class), ArgumentMatchers.<BodyHandler<String>>any()))
                .thenReturn(response);

        assertThat(underTest.getKoalaLocation(" Monday & ")).isEqualTo("Merthyr Tydfil");

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(requestCaptor.capture(), ArgumentMatchers.<BodyHandler<String>>any());
        assertThat(requestCaptor.getValue().uri().getRawQuery()).isEqualTo("day=+Monday+%26+");
    }
}
