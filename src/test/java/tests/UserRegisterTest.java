package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
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
    @Severity(SeverityLevel.BLOCKER)
    @Description("This test checks registration with already used email")
    @DisplayName("Test negative registration user")
    @Tag("negative")
    @TmsLink("MV-113")
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
    @Severity(SeverityLevel.TRIVIAL)
    @Description("This test checks registration with less than required character in the firstName field")
    @DisplayName("Test registration user with short firstName")
    @Tag("negative")
    @TmsLink("MV-114")
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
    @Description("This test successfully register user by random correct values")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Test positive registration user")
    @Tag("positive")
    @TmsLink("MV-115")
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
    @Severity(SeverityLevel.MINOR)
    @Description("This test checks registration with incorrect email w/o symbol @")
    @DisplayName("Test registration user with incorrect email")
    @Tag("negative")
    @TmsLink("MV-116")
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
    @Severity(SeverityLevel.NORMAL)
    @EnumSource(UserRegistrationFields.class)
    @Tag("negative")
    @TmsLink("MV-117")
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
    @Severity(SeverityLevel.NORMAL)
    @Description("This test checks registration with more than expected character in the firstName field")
    @DisplayName("Test registration user with long firstName")
    @Tag("negative")
    @TmsLink("MV-118")
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
