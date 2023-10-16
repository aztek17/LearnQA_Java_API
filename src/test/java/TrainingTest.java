import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class TrainingTest {
    @Test
    public void testParseJson() {
        JsonPath response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        String message = response.get("messages[1].message");
        System.out.println("Текст второго сообщения: " + message);

    }

    @Test
    public void testRedirect() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String locationHeader = response.getHeader("Location");
        System.out.println("Редирект ведет на андрес: " + locationHeader);
    }

    @Test
    public void testLongRedirect() {
        String url = "https://playground.learnqa.ru/api/long_redirect";
        int countRedirect = 0;
        int statusCode = 0;

        while (statusCode != 200) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(url)
                    .andReturn();

            statusCode = response.getStatusCode();
            System.out.println("Status Code: " + statusCode);
            if (statusCode != 200) {
                url = response.getHeader("Location");
                System.out.println("Редирект на адрес: " + url);
                System.out.println("Количество редиректов: " + ++countRedirect);
                System.out.println("__________________");
            }
        }
    }

    @Test
    public void tokenTest() {
        JsonPath response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String Token = response.get("token");
        int timeForWait = response.get("seconds");

        Map<String, String> headerToken = new HashMap<>();
        headerToken.put("token", Token);

        JsonPath statusTaskIsNotReady = RestAssured
                .given()
                .params(headerToken)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        checkTaskStatus(statusTaskIsNotReady, "Job is NOT ready");
        waitTime(timeForWait);

        JsonPath statusTaskIsReady = RestAssured
                .given()
                .params(headerToken)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        checkTaskStatus(statusTaskIsReady, "Job is ready");
        checkTaskResult(statusTaskIsReady);
    }

    private void waitTime(int seconds) {
        System.out.println("Результат задачи будет готов через " + seconds + " секунд");
        try {
            Thread.sleep(Duration.ofSeconds(seconds).toMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkTaskStatus(JsonPath response, String expectStatus) {
        String taskStatus = response.get("status");
        if (!taskStatus.equals(expectStatus)) {
            System.out.println("Статус задачи: " + taskStatus + "Ожидали статус: " + expectStatus);
        } else {
            System.out.println("Статус в запросе задачи: " + taskStatus);
        }
    }

    private void checkTaskResult(JsonPath response) {
        String result = response.get("result");
        if (result == null) {
            System.out.println("Результат выполнения задачи: " + result + "Ожидали что результат не будет пустым");
        } else {
            System.out.println("Результат выполнения задачи: " + result);
        }
    }
}
