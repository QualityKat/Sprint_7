package CourierTest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@Epic("Courier Management")
@Feature("Courier Creation")
public class CourierTest {

    private Gson gson; // Создаем экземпляр Gson

    private int courierId = -1; // Глобальная переменная для хранения ID курьера
    private CourierHelper courierHelper = new CourierHelper(); // Экземпляр вспомогательного класса

    @After
    public void tearDown() {
        // Удаление курьера после каждого теста, если ID был получен
        if (courierId != -1) {
            courierHelper.deleteCourier(courierId);  // Удаление курьера
        }
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        gson = new GsonBuilder().setPrettyPrinting().create(); // Инициализация gson
    }

    //Тест что курьера можно создать
    @Test
    @Story("Create a new courier")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a new courier is possible and returns the correct response")
    public void testCreateCourierIsPossible() {
        String login = "qazhof"; // Логин
        String password = "1234"; // Пароль
        String body = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"saske\" }";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");
        // Проверяем код ответа
        assertThat(response.getStatusCode(), is(201));

        // Проверяем, что тело ответа содержит "ok: true"
        assertThat(response.jsonPath().get("ok"), is(true));

        // Авторизуемся, чтобы получить ID курьера
        courierId = courierHelper.getCourierId(login, password); // Сохраняем ID курьера

        // Проверяем ID курьера получен корректно
        assertThat(courierId, is(not(-1))); // Убедитесь, что ID не -1
    }

    //Тест что нельзя создать двух одинаковых курьеров
    //и если создать пользователя с логином, который уже есть, возвращается ошибка
    @Test
    @Story("Prevent duplicate courier creation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that creating a courier with the same login returns an error")
    public void testErrorCreateTheSameCourier() {
        String login = "qazhof"; // Логин
        String password = "1234"; // Пароль
        String body = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"saske\" }";

        // Отправляем первый запрос для создания курьера
        Response firstResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");

        // Проверяем код ответа первого запроса
        assertThat(firstResponse.getStatusCode(), is(201));
        System.out.println("Курьер успешно создан. Код ответа: " + firstResponse.getStatusCode());

        // Отправляем второй запрос для создания того же курьера
        Response secondResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");

        // Используем метод printResponse из CourierHelper для вывода второго ответа
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        courierHelper.printResponse(secondResponse, gson); // Вызов метода для второго запроса

        // Код ответа второго запроса
        assertThat(secondResponse.getStatusCode(), is(409)); // Ожидаем ошибку 409

        // Ожидаемое сообщение об ошибке
        String expectedMessage = "Этот логин уже используется. Попробуйте другой.";

        // Проверяем правильное сообщение об ошибке
        assertThat(secondResponse.jsonPath().getString("message"), is(expectedMessage));

        // Авторизуемся, чтобы получить ID курьера
        courierId = courierHelper.getCourierId(login, password); // Сохраняем ID курьера

        // Проверяем ID курьера получен корректно
        assertThat(courierId, is(not(-1))); // Убедитесь, что ID не -1
    }

    //Тест чтобы создать курьера, нужно передать в ручку все обязательные поля
    @Test
    @Story("Validate required fields for courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that all required fields are present when creating a courier")
    public void testCreateCourierWithAllRequiredFields() {
        // Тело запроса
        String login = "qazhof";
        String password = "1234";
        String body = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"saske\" }";

        // Отправляем POST запрос
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");

        // Используем метод printResponse из CourierHelper для вывода ответа
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        courierHelper.printResponse(response, gson); // Вызов метода

        // Код ответа
        assertThat(response.getStatusCode(), is(201));

        // Проверяем, что тело ответа содержит "ok: true"
        assertThat(response.jsonPath().get("ok"), is(true));

        //Авторизуемся, чтобы получить ID курьера
        courierId = courierHelper.getCourierId(login, password); // Сохраняем ID курьера

        // Проверяем ID курьера получен корректно
        assertThat(courierId, is(not(-1))); // Убедитесь, что ID не -1
    }

    //Тест на запрос создание курьера, возвращает правильный код ответа
    @Test
    @Story("Validate status code 201 for successful courier creation")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify that creating a new courier returns status code 201")
    public void testCreateCourierCode201() {
        String login = "qazhof"; // Логин
        String password = "1234"; // Пароль
        String body = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"saske\" }";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");

        // Выводим код ответа на экран
        System.out.println("Код ответа: " + response.getStatusCode());

        // Код ответа
        assertThat(response.getStatusCode(), is(201));

        // Авторизуемся, чтобы получить ID курьера
        courierId = courierHelper.getCourierId(login, password); // Сохраняем ID курьера

        // Проверяем ID курьера
        assertThat(courierId, is(not(-1))); // Убедитесь, что ID не -1
    }

    //Тест на успешный запрос создания курьера, возвращает ok: true
    @Test
    @Story("Validate 'ok: true' for successful courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a successful courier creation returns 'ok: true' in the response")
    public void testCreateCourierOkTrue() {
        // Тело запроса
        String login = "qazhof"; // Логин
        String password = "1234"; // Пароль
        String body = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"saske\" }";

        // Отправляем POST запрос
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");

        // Используем метод printResponse из CourierHelper для вывода ответа
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        courierHelper.printResponse(response, gson); // Вызов метода

        // Проверяем, что тело ответа содержит "ok: true"
        assertThat(response.jsonPath().get("ok"), is(true));

        // Авторизуемся, чтобы получить ID курьера
        courierId = courierHelper.getCourierId(login, password); // Сохраняем ID курьера

        // Проверяем ID курьера
        assertThat(courierId, is(not(-1))); // Убедитесь, что ID не -1
    }

    //Тест если одного из полей нет, запрос возвращает ошибку
    // Тест 1: Пропущено поле login
    @Test
    @Story("Validate error for missing required fields in courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a courier without a login returns an error")
    public void testCreateCourierWithoutLogin() {
        String bodyWithoutLogin = "{ \"password\": \"1234\", \"firstName\": \"saske\" }";

        // Сообщение об ошибке
        String expectedMessage = "Недостаточно данных для создания учетной записи";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bodyWithoutLogin)
                .when()
                .post("/api/v1/courier");

        // Используем метод printResponse из CourierHelper для вывода ответа
        courierHelper.printResponse(response, gson);

        // Код ответа и сообщение
        assertThat(response.getStatusCode(), is(400));
        System.out.println("Курьер не создан: пропущено поле login");
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }

    //Тест если одного из полей нет, запрос возвращает ошибку
    // Тест 2: Пропущено поле password
    @Test
    @Story("Validate error for missing required fields in courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a courier without a password returns an error")
    public void testCreateCourierWithoutPassword() {
        String login = "qazhof" + System.currentTimeMillis(); // Уникальный логин
        String bodyWithoutPassword = "{ \"login\": \"" + login + "\", \"firstName\": \"saske\" }";

        // Сообщение об ошибке
        String expectedMessage = "Недостаточно данных для создания учетной записи";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bodyWithoutPassword)
                .when()
                .post("/api/v1/courier");

        // Используем метод printResponse из CourierHelper для вывода ответа
        courierHelper.printResponse(response, gson);

        // Код ответа и сообщение
        assertThat(response.getStatusCode(), is(400));
        System.out.println("Курьер не создан: пропущено поле password");
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }

    //Тест 3: Пропущено поле firstName
    //*Тест не проходит: баг*
    @Test
    @Story("Validate error for missing required fields in courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a courier without a first name returns an error")
    public void testCreateCourierWithoutFirstName() {
        String login = "qazhof" + System.currentTimeMillis(); // Уникальный логин
        String bodyWithoutFirstName = "{ \"login\": \"" + login + "\", \"password\": \"1234\" }";

        // Сообщение об ошибке
        String expectedMessage = "Недостаточно данных для создания учетной записи";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bodyWithoutFirstName)
                .when()
                .post("/api/v1/courier");

        // Используем метод printResponse из CourierHelper для вывода ответа
        courierHelper.printResponse(response, gson);

        // Код ответа и сообщение
        assertThat(response.getStatusCode(), is(400));
        System.out.println("Курьер не создан: пропущено поле firstName");
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }
}
