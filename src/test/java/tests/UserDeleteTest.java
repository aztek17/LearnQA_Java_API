package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Check delete user cases")
@Feature("Delete user")
public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("This test checks delete reserved users")
    @DisplayName("Delete reserved user")
    public void testDeleteReservedUser() {
        // AUTHORIZATION RESERVED USER
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String authHeader = this.getHeader(responseGetAuth, "x-csrf-token");
        String authCookie = this.getCookie(responseGetAuth, "auth_sid");
        int idUser = getIntFromJson(responseGetAuth, "user_id");

        // DELETE RESERVED USER
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest(
                        "https://playground.learnqa.ru/api/user/" + idUser,
                        authHeader,
                        authCookie
                );

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertResponseTextEquals(responseDeleteUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test
    @Description("This test successfully delete user")
    @DisplayName("Positive delete user")
    public void testDeleteUser() {
        // CREATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/",
                        userData
                );
        int newUserId = this.getIntFromJson(responseCreateAuth, "id");
        String emailNewUsers = userData.get("email");
        String passwordNewUser = userData.get("password");

        // AUTHORIZATION USER
        Map<String, String> authData = new HashMap<>();
        authData.put("email", emailNewUsers);
        authData.put("password", passwordNewUser);

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/login",
                        authData
                );

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        // DELETE USER
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest(
                        "https://playground.learnqa.ru/api/user/" + newUserId,
                        header,
                        cookie
                );

        Assertions.assertResponseCodeEquals(responseDeleteUser, 200);

        // GET DELETED USER
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + newUserId,
                        header,
                        cookie
                );

        Assertions.assertResponseCodeEquals(responseUserData, 404);
        Assertions.assertResponseTextEquals(responseUserData, "User not found");
    }

    @Test
    @Description("This test delete user by authorized another user")
    @DisplayName("Delete user by authorized another user")
    public void testDeleteAsAnotherUser() {
        // CREATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/",
                        userData
                );
        int newUserId = this.getIntFromJson(responseCreateAuth, "id");

        // AUTHORIZATION ANOTHER USER
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String authHeader = this.getHeader(responseGetAuth, "x-csrf-token");
        String authCookie = this.getCookie(responseGetAuth, "auth_sid");

        // DELETE CREATED USER
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest(
                        "https://playground.learnqa.ru/api/user/" + newUserId,
                        authHeader,
                        authCookie
                );

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
    }
}
