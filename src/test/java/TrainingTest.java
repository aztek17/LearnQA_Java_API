import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
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
}
