package LoginCourierTest;
import CourierTest.CourierHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

//Тесты без создания отдельного класса с методами (как один из вариантов)

@Epic("Courier Management")
@Feature("Courier Login")
public class LoginCourierTest {

    private Gson gson; // Создаем экземпляр Gson

    private int courierId = -1; // Переменная для хранения ID курьера
    private CourierTest.CourierHelper courierHelper = new CourierHelper(); // Экземпляр вспомогательного класса

    @After
    public void tearDown() {
        // Удаление курьера после каждого теста, если ID был получен
        if (courierId != -1) {
            courierHelper.deleteCourier(courierId);  // Удаление курьера
        }
    }

    @Before
    @Step("Set up test environment")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        gson = new GsonBuilder().setPrettyPrinting().create(); // Инициализация gson
    }

    //курьер может авторизоваться
    //для авторизации нужно передать все обязательные поля
    //успешный запрос возвращает id
    @Test
    @DisplayName("Courier can be created and login")
    @Step("Create courier and verify login")
    public void testCourierCanBeCreatedAndLogin() {
        // Данные для создания курьера
        String login = "qazhof";
        String password = "1234";
        String body = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"saske\" }";

        // Отправляем запрос на создание курьера
        Response createResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");

        // Проверяем код ответа на создание курьера
        assertThat(createResponse.getStatusCode(), is(201));
        System.out.println("Курьер успешно создан. Код ответа: " + createResponse.getStatusCode());

        // Данные для авторизации
        String loginBody = "{ \"login\": \"qazhof\", \"password\": \"1234\" }";

        // Отправляем запрос на авторизацию курьера
        Response loginResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(loginBody)
                .when()
                .post("/api/v1/courier/login");

        // Проверяем код ответа на авторизацию
        assertThat(loginResponse.getStatusCode(), is(200));

        // Проверяем, что ответ содержит id
        assertThat(loginResponse.jsonPath().get("id"), is(notNullValue()));

        // Выводим код ответа и тело ответа в формате JSON на экран
        System.out.println("Код ответа на авторизацию: " + loginResponse.getStatusCode());
        System.out.println("Тело ответа на авторизацию: " + loginResponse.asString());

        // Авторизуемся, чтобы получить ID курьера
        courierId = courierHelper.getCourierId(login, password); // Сохраняем ID курьера

        // Проверяем ID курьера получен корректно
        assertThat(courierId, is(not(-1))); // Что ID не -1
    }

    //Тест что система вернёт ошибку, если неправильно указать логин или пароль
    @Test
    @DisplayName("Login with wrong credentials should fail")
    @Step("Test courier login with wrong credentials")
    public void testWithWrongLoginOrPasswordCourier() {
        // Данные для создания курьера
        String login = "qazhof";
        String password = "1234";
        String body = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"saske\" }";

        // Отправляем запрос на создание курьера
        Response createResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");

        // Проверяем код ответа на создание курьера
        assertThat(createResponse.getStatusCode(), is(201));
        System.out.println("Курьер создан. Код ответа: " + createResponse.getStatusCode());

        // Тест 1: Неверный логин и пароль
        String bodyWrongCredentials = "{ \"login\": \"wrongUser\", \"password\": \"4321\" }";
        Response responseWrongCredentials = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bodyWrongCredentials)
                .when()
                .post("/api/v1/courier/login");

        // Проверяем код ответа и сообщение
        assertThat(responseWrongCredentials.getStatusCode(), is(404));
        assertThat(responseWrongCredentials.jsonPath().getString("message"), is("Учетная запись не найдена"));

        // Вывод на экран для неверных учетных данных
        System.out.println("Wrong login and password:");
        System.out.println("Response Code: " + responseWrongCredentials.getStatusCode());
        System.out.println("Response Body: " + responseWrongCredentials.asString());

        // Тест 2: Неверный логин
        String bodyWrongLogin = "{ \"login\": \"wrongUser\", \"password\": \"1234\" }";
        Response responseWrongLogin = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bodyWrongLogin)
                .when()
                .post("/api/v1/courier/login");

        // Проверяем код ответа и сообщение
        assertThat(responseWrongLogin.getStatusCode(), is(404));
        assertThat(responseWrongLogin.jsonPath().getString("message"), is("Учетная запись не найдена"));

        // Вывод на экран для неверного логина
        System.out.println("Wrong login:");
        System.out.println("Response Code: " + responseWrongLogin.getStatusCode());
        System.out.println("Response Body: " + responseWrongLogin.asString());

        // Тест 3: Неверный пароль
        String bodyWrongPassword = "{ \"login\": \"qazhof\", \"password\": \"1256\" }"; // Существующий логин
        Response responseWrongPassword = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bodyWrongPassword)
                .when()
                .post("/api/v1/courier/login");

        // Проверяем код ответа и сообщение
        assertThat(responseWrongPassword.getStatusCode(), is(404));
        assertThat(responseWrongPassword.jsonPath().getString("message"), is("Учетная запись не найдена"));

        // Вывод на экран для неверного пароля
        System.out.println("Wrong password:");
        System.out.println("Response Code: " + responseWrongPassword.getStatusCode());
        System.out.println("Response Body: " + responseWrongPassword.asString());

        // Авторизуемся, чтобы получить ID курьера
        courierId = courierHelper.getCourierId(login, password); // Сохраняем ID курьера

        // Проверяем что ID курьера получен корректно
        assertThat(courierId, is(not(-1))); // Что ID не -1
    }

    //Тест если какого-то поля нет, запрос возвращает ошибку
    //*Тест не проходит: при отсутствии поля password = баг*
    @Test
    @DisplayName("Missing required fields returns error")
    @Step("Test missing login or password during courier login")
    public void testMissingRequiredFieldsCourier() {
        // Данные для создания курьера
        String login = "qazhof";
        String password = "1234";
        String body = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"saske\" }";

        // Отправляем запрос на создание курьера
        Response createResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");

        // Проверяем код ответа на создание курьера
        assertThat(createResponse.getStatusCode(), is(201));
        System.out.println("Курьер успешно создан. Код ответа: " + createResponse.getStatusCode());

        // Включаем авторизацию и получение ID курьера
        try {
            // Авторизуемся, чтобы получить ID курьера
            courierId = courierHelper.getCourierId(login, password); // Сохраняем ID курьера

            // Проверяем, что ID курьера получен корректно
            assertThat(courierId, is(not(-1))); // Что ID не -1

            // Тест 1: Отсутствует поле "login"
            String bodyWithoutLogin = "{ \"password\": \"1234\" }";
            Response responseWithoutLogin = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .body(bodyWithoutLogin)
                    .when()
                    .post("/api/v1/courier/login");

            // Ожидаемое сообщение об ошибке
            String expectedMessage = "Недостаточно данных для входа";

            // Проверяем код ответа и сообщение
            assertThat(responseWithoutLogin.getStatusCode(), is(400));
            assertThat(responseWithoutLogin.jsonPath().getString("message"), is(expectedMessage));

            System.out.println("Тест на отсутствие логина. Код ответа: " + responseWithoutLogin.getStatusCode());

            // Тест 2: Отсутствует поле "password"
            String bodyWithoutPassword = "{ \"login\": \"qazhof\" }"; // Существующий логин
            Response responseWithoutPassword = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .body(bodyWithoutPassword)
                    .when()
                    .post("/api/v1/courier/login");

            // Проверяем код ответа и сообщение
            assertThat(responseWithoutPassword.getStatusCode(), is(400));
            assertThat(responseWithoutPassword.jsonPath().getString("message"), is(expectedMessage));

            System.out.println("Тест на отсутствие пароля. Код ответа: " + responseWithoutPassword.getStatusCode());

        } finally {
            // Проверяем, был ли курьер создан и авторизован
            if (courierId != -1) {
                // Удаляем курьера
                Response deleteResponse = RestAssured.given()
                        .header("Content-Type", "application/json")
                        .when()
                        .delete("/api/v1/courier/" + courierId);

                // Проверяем код ответа на удаление курьера
                if (deleteResponse.getStatusCode() == 200) {
                    System.out.println("Курьер удален. Код ответа: " + deleteResponse.getStatusCode());
                } else {
                    System.err.println("Ошибка при удалении курьера. Код ответа: " + deleteResponse.getStatusCode());
                    System.err.println("Тело ответа: " + deleteResponse.asString());
                }
            } else {
                System.err.println("Ошибка: не удалось получить ID курьера, удаление невозможно.");
            }
        }
    }

    //Тест если авторизоваться под несуществующим пользователем, запрос возвращает ошибку
    @Test
    @DisplayName("Login non-existent user returns error")
    @Step("Test login for non-existent courier")
    public void testLoginNonExistentUser() {
        // Данные для авторизации с несуществующим пользователем
        String bodyNonExistentUser = "{ \"login\": \"Adamoff\", \"password\": \"12347\" }";

        // Отправляем запрос на авторизацию
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bodyNonExistentUser)
                .when()
                .post("/api/v1/courier/login");

        // Проверяем код ответа и сообщение
        assertThat(response.getStatusCode(), is(404));
        assertThat(response.jsonPath().getString("message"), is("Учетная запись не найдена"));

        // Вывод на экран: код ответа и тело ответа в формате JSON
        System.out.println("Not existent user login:");
        System.out.println("Response Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.asString());
    }
}
