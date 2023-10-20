import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThirdLesson {

    @ParameterizedTest
    @ValueSource(strings = {"This line has more than fifteen characters", "Short line"})
    public void stringLengthTest(String word) {
        assertTrue(word.length() > 15, "The length of the string should be more than 15 symbols");
    }

    @Test
    public void checkCookie() {
        String expectedKey = "HomeWork";
        String expectedValue = "hw_value";

        Map<String, String> cookieExpected = new HashMap<>();
        cookieExpected.put(expectedKey, expectedValue);

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        Map<String, String> cookiesActual = response.getCookies();
        assertTrue(cookiesActual.containsKey(expectedKey), "Response doesn't have " + expectedKey + " cookie");
        assertEquals(cookieExpected.get(expectedKey), response.getCookie(expectedKey),
                "Cookie value in response is not equal " + expectedValue);

    }
}
