package uk.gov.laa.adamlb.pactdemo.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(KoalaController.class)
@Import(KoalaService.class)
class KoalaControllerTest {

    private static final String LOCATION_ENDPOINT = "/koala/location";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void validDayReturnsLocation() throws Exception {
        mockMvc.perform(get(LOCATION_ENDPOINT).param("day", "Monday"))
                .andExpect(status().isOk())
                .andExpect(content().string("Merthyr Tydfil"));
    }

    @Test
    void whitespaceAroundDayIsTrimmed() throws Exception {
        mockMvc.perform(get(LOCATION_ENDPOINT).param("day", " Monday "))
                .andExpect(status().isOk())
                .andExpect(content().string("Merthyr Tydfil"));
    }

    @Test
    void invalidDayReturnsBadRequest() throws Exception {
        mockMvc.perform(get(LOCATION_ENDPOINT).param("day", "Humpday")).andExpect(status().isBadRequest());
    }

    @Test
    void lowercaseDayReturnsLocation() throws Exception {
        mockMvc.perform(get(LOCATION_ENDPOINT).param("day", "monday"))
                .andExpect(status().isOk())
                .andExpect(content().string("Merthyr Tydfil"));
    }

    @Test
    void blankDayReturnsBadRequest() throws Exception {
        mockMvc.perform(get(LOCATION_ENDPOINT).param("day", "   ")).andExpect(status().isBadRequest());
    }

    @Test
    void missingDayReturnsBadRequest() throws Exception {
        mockMvc.perform(get(LOCATION_ENDPOINT)).andExpect(status().isBadRequest());
    }
}
