import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

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
}
