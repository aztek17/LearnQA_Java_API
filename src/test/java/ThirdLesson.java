import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    @Test
    public void checkHeader() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        Headers headers = response.getHeaders();
        String time = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC));

        Map<String, String> expectedHeaders = getStringStringMap(time);

        for (Header header : headers
        ) {
            assertTrue(expectedHeaders.containsKey(header.getName()), "The expected header '" + header.getName() + "' is missing");
            assertEquals(expectedHeaders.get(header.getName()), header.getValue(), "The header's value '" + header.getName() + "' is different from expected");
        }
    }

    private static Map<String, String> getStringStringMap(String time) {
        Map<String, String> expectedHeaders = new HashMap<>();
        expectedHeaders
                .put("Date", time);
        expectedHeaders
                .put("Content-Type", "application/json");
        expectedHeaders
                .put("Content-Length", "15");
        expectedHeaders
                .put("Connection", "keep-alive");
        expectedHeaders
                .put("Keep-Alive", "timeout=10");
        expectedHeaders
                .put("Server", "Apache");
        expectedHeaders
                .put("x-secret-homework-header", "Some secret value");
        expectedHeaders
                .put("Cache-Control", "max-age=0");
        expectedHeaders
                .put("Expires", time);
        return expectedHeaders;
    }
}
