package gt.edu.uinsight.academicperiod.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class PeriodStatusTest {

    @DisplayName("RN-06: solo PLANNED->ACTIVE y ACTIVE->CLOSED son transiciones validas")
    @ParameterizedTest(name = "{0} -> {1} = {2}")
    @CsvSource({
            "PLANNED, ACTIVE,  true",
            "ACTIVE,  CLOSED,  true",
            "PLANNED, PLANNED, false",
            "PLANNED, CLOSED,  false",
            "ACTIVE,  PLANNED, false",
            "ACTIVE,  ACTIVE,  false",
            "CLOSED,  PLANNED, false",
            "CLOSED,  ACTIVE,  false",
            "CLOSED,  CLOSED,  false"
    })
    void canTransitionTo(PeriodStatus from, PeriodStatus to, boolean expected) {
        assertThat(from.canTransitionTo(to)).isEqualTo(expected);
    }
}
