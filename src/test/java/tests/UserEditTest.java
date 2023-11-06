package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Check edit user cases")
@Feature("Edit user")
public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    int userId;
    String email;
    String password;

    @BeforeEach
    //GENERATE USER
    public void createUser() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/",
                        userData
                );
        this.userId = this.getIntFromJson(responseCreateAuth, "id");
        this.email = userData.get("email");
        this.password = userData.get("password");
    }

    @Test
    @Description("This test checks changed users firstname by authorized yourself")
    @DisplayName("Change user by authorized yourself")
    public void testEditJustCreatedTest() {
        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", this.email);
        authData.put("password", this.password);

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/login",
                        authData
                );

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequest(
                        "https://playground.learnqa.ru/api/user/" + this.userId,
                        header,
                        cookie,
                        editData
                );
        Assertions.assertResponseCodeEquals(responseEditUser, 200);

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + this.userId,
                        header,
                        cookie
                );

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    @Description("This test checks changed users firstname without previously authorize")
    @DisplayName("Change user without authorize")
    public void testEditUserWithoutAuthorize() {
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequestWithoutAuthorize(
                        "https://playground.learnqa.ru/api/user/" + this.userId,
                        editData
                );

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "Auth token not supplied");
    }

    @Test
    @Description("This test checks changed users firstname with authorize strangers user")
    @DisplayName("Change not your user")
    public void testEditNotYourUsers() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String authCookie = this.getCookie(responseGetAuth, "auth_sid");
        String authToken = this.getHeader(responseGetAuth, "x-csrf-token");
        Assertions.jsonValueByNameNotEquals(responseGetAuth, "user_id", this.userId);

        //EDIT
        String newName = "Changed New Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequest(
                        "https://playground.learnqa.ru/api/user/" + this.userId,
                        authCookie,
                        authToken,
                        editData
                );

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "Auth token not supplied");

    }
}
