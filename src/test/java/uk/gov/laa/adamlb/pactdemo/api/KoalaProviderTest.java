package uk.gov.laa.adamlb.pactdemo.api;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import au.com.dius.pact.provider.spring.spring7.PactVerificationSpring7Provider;
import au.com.dius.pact.provider.spring.spring7.Spring7MockMvcTestTarget;
import uk.gov.laa.adamlb.pactdemo.PactDemo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@Provider("koala-api")
@PactBroker(
        url = "${pactbroker.url:http://localhost:9292}",
        providerBranch = "${pact.provider.branch:}",
        authentication =
                @PactBrokerAuth(username = "${pactbroker.auth.username:}", password = "${pactbroker.auth.password:}"))
@SpringBootTest(classes = PactDemo.class)
@AutoConfigureMockMvc(addFilters = false)
final class KoalaProviderTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setTarget(PactVerificationContext context) {
        context.setTarget(new Spring7MockMvcTestTarget(mockMvc));
    }

    @TestTemplate
    @ExtendWith(PactVerificationSpring7Provider.class)
    void verifyPact(PactVerificationContext context) {
        context.verifyInteraction();
    }

    // This `@State` name must match the state name in the consumer pact file.
    @State("Koala is in Merthyr Tydfil on Monday")
    public void koalaIsInMerthyrTydfilOnMonday() {
        // These can be empty because we don't rely on a database or any external state.
    }

    @State("Koala is in his tree on a weekend")
    public void koalaIsInHisTreeOnAWeekend() {}

    @State("An invalid weekday produces an error")
    public void anInvalidWeekdayProducesAnError() {}
}
