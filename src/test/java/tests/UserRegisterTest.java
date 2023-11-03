package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.HashMap;
import java.util.Map;

@Epic("Registration cases")
@Feature("Registration")
public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("This test checks registration with already used email")
    @DisplayName("Test negative registration user")
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/",
                        userData
                );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Test
    @Description("This test successfully register user by random correct values")
    @DisplayName("Test positive registration user")
    public void testCreateUserSuccessfully() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/",
                        userData
                );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test
    @Description("This test checks registration with incorrect email w/o symbol @")
    @DisplayName("Test registration user with incorrect email")
    public void testCreateUserWithIncorrectEmail() {
        String email = "petrovgmail.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/",
                        userData
                );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }

    @Description("This test checks registration w/o sending required parameters from list")
    @DisplayName("Test registration user w/o required parameters")
    @ParameterizedTest
    @EnumSource(UserRegistrationFields.class)
    public void testCreateUserWithoutRequiredFields(UserRegistrationFields field) {

        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.remove(field.name());

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/",
                        userData
                );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The following required " +
                "params are missed: " + field.name());
    }

    @Test
    @Description("This test checks registration with less than required character in the firstName field")
    @DisplayName("Test registration user with short firstName")
    public void testCreateUserWithShortName() {
        String firstName = DataGenerator.getRandomString(1);
        System.out.println(firstName);

        Map<String, String> userData = new HashMap<>();
        userData.put("firstName", firstName);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/",
                        userData
                );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'firstName' field is too short");
    }

    @Test
    @Description("This test checks registration with more than expected character in the firstName field")
    @DisplayName("Test registration user with long firstName")
    public void testCreateUserWithLongName() {
        String firstName = DataGenerator.getRandomString(251);

        Map<String, String> userData = new HashMap<>();
        userData.put("firstName", firstName);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/",
                        userData
                );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'firstName' field is too long");
    }
}
