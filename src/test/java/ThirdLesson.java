import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThirdLesson {

    @ParameterizedTest
    @ValueSource(strings = {"This line has more than fifteen characters", "Short line"})
    public void stringLengthTest(String word) {
        assertTrue(word.length() > 15, "The length of the string should be more than 15 symbols");
    }
}
