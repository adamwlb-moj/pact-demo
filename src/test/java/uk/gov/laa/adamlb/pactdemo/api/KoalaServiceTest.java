package uk.gov.laa.adamlb.pactdemo.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class KoalaServiceTest {

    private final KoalaService underTest = new KoalaService();

    @ParameterizedTest
    @CsvSource({"Monday, Merthyr Tydfil", "Tuesday, Glasgow", "Wednesday, Leeds", "Thursday, London", "Friday, Belfast"
    })
    void validWeekdayReturnsLocation(String day, String expectedLocation) {
        String result = underTest.getLocation(day);
        assertThat(expectedLocation).isEqualTo(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Saturday", "Sunday"})
    void weekendReturnsEucalyptusTree(String day) {
        String result = underTest.getLocation(day);
        assertThat("Eucalyptus Tree").isEqualTo(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Humpday", "Funday", "Someday"})
    void invalidDayThrowsException(String day) {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> underTest.getLocation(day));
    }

    @ParameterizedTest
    @CsvSource({"monday, Merthyr Tydfil", "TUESDAY, Glasgow", "wEdNeSdAy, Leeds"})
    void dayIsCaseInsensitive(String day, String expectedLocation) {
        assertThat(underTest.getLocation(day)).isEqualTo(expectedLocation);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void blankDayThrowsException(String day) {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> underTest.getLocation(day));
    }

    @Test
    void nullDayThrowsException() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> underTest.getLocation(null));
    }
}
