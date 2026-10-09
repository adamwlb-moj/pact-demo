package uk.gov.laa.adamlb.pactdemo.client;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.laa.adamlb.pactdemo.client.CrimeAssessClient.ERROR_STRING;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "koala-api", pactVersion = PactSpecVersion.V3)
final class CrimeAssessConsumerTest {

    @Pact(consumer = "crime-assess-team")
    public RequestResponsePact mondayLocation(PactDslWithProvider builder) {
        return builder.given("Koala is in Merthyr Tydfil on Monday")
                .uponReceiving("a request for the koala's location on Monday")
                .path("/koala/location")
                .method("GET")
                .query("day=Monday")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "text/plain"))
                .body("Merthyr Tydfil")
                .toPact();
    }

    @Pact(consumer = "crime-assess-team")
    public RequestResponsePact weekendLocation(PactDslWithProvider builder) {
        return builder.given("Koala is in his tree on a weekend")
                .uponReceiving("a request for the koala's location on Saturday")
                .path("/koala/location")
                .method("GET")
                .query("day=Saturday")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "text/plain"))
                .body("Eucalyptus Tree")
                .toPact();
    }

    @Pact(consumer = "crime-assess-team")
    public RequestResponsePact invalidDay(PactDslWithProvider builder) {
        return builder.given("An invalid weekday produces an error")
                .uponReceiving("a request for the koala's location on an invalid day")
                .path("/koala/location")
                .method("GET")
                .query("day=Humpday")
                .willRespondWith()
                .status(400)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "mondayLocation")
    void mondayReturnsLocation(MockServer mockServer) {
        CrimeAssessClient client = clientFor(mockServer);
        assertThat(client.getKoalaLocation("Monday")).isEqualTo("Merthyr Tydfil");
    }

    @Test
    @PactTestFor(pactMethod = "weekendLocation")
    void saturdayReturnsTree(MockServer mockServer) {
        CrimeAssessClient client = clientFor(mockServer);
        assertThat(client.getKoalaLocation("Saturday")).isEqualTo("Eucalyptus Tree");
    }

    @Test
    @PactTestFor(pactMethod = "invalidDay")
    void invalidDayReturnsErrorString(MockServer mockServer) {
        CrimeAssessClient client = clientFor(mockServer);
        assertThat(client.getKoalaLocation("Humpday")).isEqualTo(ERROR_STRING);
    }

    private CrimeAssessClient clientFor(MockServer mockServer) {
        return new CrimeAssessClient(HttpClient.newHttpClient(), URI.create(mockServer.getUrl()));
    }
}
