package logincouriertest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import couriertest.api.CourierApi;
import couriertest.api.CourierData;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Courier Management")
@Feature("Courier Login")
public class LoginCourierTest {

    private Gson gson; // Создаем экземпляр Gson
    private int courierId = -1; // Переменная для хранения ID курьера
    private CourierApi courierApi = new CourierApi(); // Экземпляр класса API

    @After
    public void tearDown() {
        // Удаление курьера после каждого теста, если ID был получен
        if (courierId != -1) {
            courierApi.deleteCourier(courierId); // Удаление курьера
        }
    }

    @Before
    @Step("Set up test environment")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        gson = new GsonBuilder().setPrettyPrinting().create(); // Инициализация gson
    }

    @Test
    @DisplayName("Courier can be created and login")
    @Step("Create courier and verify login")
    public void testCourierCanBeCreatedAndLogin() {
        // Данные для создания курьера
        CourierData courierData = new CourierData("qazhof", "1234", "saske");

        // Создаем курьера
        Response createResponse = courierApi.createCourier(courierData);
        assertThat(createResponse.getStatusCode(), is(201));
        System.out.println("Курьер успешно создан. Код ответа: " + createResponse.getStatusCode());

        // Авторизуемся, чтобы получить ID курьера
        courierId = courierApi.authorizeCourier(courierData);
        assertThat(courierId, is(not(-1))); // Проверяем, что ID курьера получен корректно
    }

    @Test
    @DisplayName("Login with wrong credentials should fail")
    @Step("Test courier login with wrong credentials")
    public void testWithWrongLoginOrPasswordCourier() {
        // Данные для создания курьера
        CourierData courierData = new CourierData("qazhof", "1234", "saske");

        // Создаем курьера
        Response createResponse = courierApi.createCourier(courierData);
        assertThat(createResponse.getStatusCode(), is(201));
        System.out.println("Курьер создан. Код ответа: " + createResponse.getStatusCode());

        // Тест 1: Неверный логин и пароль
        CourierData wrongCredentials = new CourierData("wrongUser ", "4321", null);
        Response responseWrongCredentials = courierApi.getCourierId(wrongCredentials);
        assertThat(responseWrongCredentials.getStatusCode(), is(404));
        assertThat(responseWrongCredentials.jsonPath().getString("message"), is("Учетная запись не найдена"));

        // Тест 2: Неверный логин
        wrongCredentials = new CourierData("wrongUser ", "1234", null);
        Response responseWrongLogin = courierApi.getCourierId(wrongCredentials);
        assertThat(responseWrongLogin.getStatusCode(), is(404));
        assertThat(responseWrongLogin.jsonPath().getString("message"), is("Учетная запись не найдена"));

        // Тест 3: Неверный пароль
        wrongCredentials = new CourierData("qazhof", "1256", null); // Существующий логин
        Response responseWrongPassword = courierApi.getCourierId(wrongCredentials);
        assertThat(responseWrongPassword.getStatusCode(), is(404));
        assertThat(responseWrongPassword.jsonPath().getString("message"), is("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Missing required fields returns error")
    @Step("Test missing login or password during courier login")
    public void testMissingRequiredFieldsCourier() {
        // Данные для создания курьера
        CourierData courierData = new CourierData("qazhof", "1234", "saske");

        // Создаем курьера
        Response createResponse = courierApi.createCourier(courierData);
        assertThat(createResponse.getStatusCode(), is(201));
        System.out.println("Курьер успешно создан. Код ответа: " + createResponse.getStatusCode());

        // Авторизуемся, чтобы получить ID курьера
        courierId = courierApi.authorizeCourier(courierData);
        assertThat(courierId, is(not(-1))); // Проверяем, что ID курьера получен корректно

        // Тест 1: Отсутствует поле "login"
        CourierData missingLogin = new CourierData(null, "1234", null);
        Response responseWithoutLogin = courierApi.getCourierId(missingLogin);
        assertThat(responseWithoutLogin.getStatusCode(), is(400));
        assertThat(responseWithoutLogin.jsonPath().getString("message"), is("Недостаточно данных для входа"));

        // Тест 2: Отсутствует поле "password"
        CourierData missingPassword = new CourierData("qazhof", null, null); // Существующий логин
        Response responseWithoutPassword = courierApi.getCourierId(missingPassword);
        assertThat(responseWithoutPassword.getStatusCode(), is(400));
        assertThat(responseWithoutPassword.jsonPath().getString("message"), is("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Login non-existent user returns error")
    @Step("Test login for non-existent courier")
    public void testLoginNonExistentUser () {
        // Данные для авторизации с несуществующим пользователем
        CourierData nonExistentUser  = new CourierData("Adamoff", "12347", null);

        // Отправляем запрос на авторизацию
        Response response = courierApi.getCourierId(nonExistentUser );
        assertThat(response.getStatusCode(), is(404));
        assertThat(response.jsonPath().getString("message"), is("Учетная запись не найдена"));

        // Вывод на экран: код ответа и тело ответа в формате JSON
        System.out.println("Not existent user login:");
        System.out.println("Response Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.asString());
    }
}