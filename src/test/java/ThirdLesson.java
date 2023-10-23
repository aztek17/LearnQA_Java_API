import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

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

    @ParameterizedTest
    @MethodSource("dataProvider")
    public void userAgentTest(String agent, String platform, String browser, String device) {
        Header userAgent = new Header("User-Agent", agent);

        JsonPath response = RestAssured
                .given()
                .when()
                .header(userAgent)
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .jsonPath();

        assertEquals(response.get("platform"), platform, errorMessage(agent, "platform"));
        assertEquals(response.get("browser"), browser, errorMessage(agent, "browser"));
        assertEquals(response.get("device"), device, errorMessage(agent, "device"));
    }

    private static Stream<Arguments> dataProvider() {
        return Stream.of(
                Arguments.of("Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30",
                        "Mobile",
                        "No",
                        "Android"),
                Arguments.of("Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1",
                        "Mobile",
                        "Chrome",
                        "iOS"),
                Arguments.of("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
                        "Googlebot",
                        "Unknown",
                        "Unknown"),
                Arguments.of("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0",
                        "Web",
                        "Chrome",
                        "No"),
                Arguments.of("Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1",
                        "Mobile",
                        "No",
                        "iPhone"));

    }

    private String errorMessage(String userAgent, String param) {
        return "User_agent: '" + userAgent + "' returned incorrect param: " + param;
    }
}
