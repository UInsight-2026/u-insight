package gt.edu.uinsight.course.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class CourseStatusTest {

    @DisplayName("Un curso alterna entre ACTIVE e INACTIVE; no transiciona al mismo estado")
    @ParameterizedTest(name = "{0} -> {1} = {2}")
    @CsvSource({
            "ACTIVE,   INACTIVE, true",
            "INACTIVE, ACTIVE,   true",
            "ACTIVE,   ACTIVE,   false",
            "INACTIVE, INACTIVE, false"
    })
    void canTransitionTo(CourseStatus from, CourseStatus to, boolean expected) {
        assertThat(from.canTransitionTo(to)).isEqualTo(expected);
    }
}
