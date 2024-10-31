package couriertest.tests;

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

public class CourierTest2 {

    private static final String DEFAULT_LOGIN = "qazhof";
    private static final String DEFAULT_PASSWORD = "1234";
    private static final String DEFAULT_FIRST_NAME = "saske";

    private Gson gson; // Создаем экземпляр Gson
    private int courierId = -1; // Глобальная переменная для хранения ID курьера
    private CourierApi courierApi; // Экземпляр класса для работы с API

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        gson = new GsonBuilder().setPrettyPrinting().create(); // Инициализация gson
        courierApi = new CourierApi(); // Инициализация API класса
    }

    @After
    public void tearDown() {
        // Удаление курьера после каждого теста, если ID был получен
        if (courierId != -1) {
            courierApi.deleteCourier(courierId);  // Удаление курьера
        }
    }

    // Тест, что курьера можно создать
    @Test
    @Story("Create a new courier")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a new courier is possible and returns the correct response")
    public void testCreateCourierIsPossible() {
        CourierData courierData = new CourierData(DEFAULT_LOGIN, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME);
        Response response = courierApi.createCourier(courierData);

        // Проверяем код ответа
        courierApi.checkStatusCode(response, 201);
        assertThat(response.jsonPath().get("ok"), is(true));

        // Авторизуемся и сохраняем ID курьера
        courierId = courierApi.getCourierId(courierData);
    }

    // Тест, что нельзя создать двух одинаковых курьеров
    @Test
    @Story("Prevent duplicate courier creation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that creating a courier with the same login returns an error")
    public void testErrorCreateTheSameCourier() {
        CourierData courierData = new CourierData(DEFAULT_LOGIN, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME);

        // Отправляем первый запрос для создания курьера
        Response firstResponse = courierApi.createCourier(courierData);
        courierApi.checkStatusCode(firstResponse, 201);

        // Отправляем второй запрос с теми же данными, чтобы попытаться создать дубликат курьера
        Response secondResponse = courierApi.createCourier(courierData);
        courierApi.checkStatusCode(secondResponse, 409);

        // Ожидаемое сообщение об ошибке
        String expectedMessage = "Этот логин уже используется. Попробуйте другой.";
        assertThat(secondResponse.jsonPath().getString("message"), is(expectedMessage));

        // Авторизуемся и получаем ID курьера для проверки
        courierId = courierApi.getCourierId(courierData);
        assertThat(courierId, is(not(-1)));
    }

    // Тест, чтобы создать курьера, нужно передать в ручку все обязательные поля
    @Test
    @Story("Validate required fields for courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that all required fields are present when creating a courier")
    public void testCreateCourierWithAllRequiredFields() {
        CourierData courierData = new CourierData(DEFAULT_LOGIN, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME);
        Response response = courierApi.createCourier(courierData);

        // Проверяем код ответа
        courierApi.checkStatusCode(response, 201);
        assertThat(response.jsonPath().get("ok"), is(true));

        // Авторизуемся и сохраняем ID курьера
        courierId = courierApi.getCourierId(courierData);
    }

    // Тест на запрос создание курьера, возвращает правильный код ответа
    @Test
    @Story("Validate status code 201 for successful courier creation")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify that creating a new courier returns status code 201")
    public void testCreateCourierCode201() {
        CourierData courierData = new CourierData(DEFAULT_LOGIN, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME);
        Response response = courierApi.createCourier(courierData);

        // Проверяем код ответа
        courierApi.checkStatusCode(response, 201);

        // Авторизуемся и сохраняем ID курьера
        courierId = courierApi.getCourierId(courierData);
    }

    // Тест на успешный запрос создания курьера, возвращает ok: true
    @Test
    @Story("Validate 'ok: true' for successful courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a successful courier creation returns 'ok: true' in the response")
    public void testCreateCourierOkTrue() {
        CourierData courierData = new CourierData(DEFAULT_LOGIN, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME);
        Response response = courierApi.createCourier(courierData);

        // Проверяем, что в теле ответа поле "ok" равно true
        assertThat(response.jsonPath().get("ok"), is(true));

        // Авторизуемся и сохраняем ID курьера
        courierId = courierApi.getCourierId(courierData);
    }

    // Тест если одного из полей нет, запрос возвращает ошибку
    // Тест 1: Пропущено поле login
    @Test
    @Story("Validate error for missing required fields in courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a courier without a login returns an error")
    public void testCreateCourierWithoutLogin() {
        CourierData courierData = new CourierData(null, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME);
        Response response = courierApi.createCourier(courierData);

        // Проверяем код ответа 400 для ошибки
        courierApi.checkStatusCode(response, 400);

        // Ожидаемое сообщение об ошибке
        String expectedMessage = "Недостаточно данных для создания учетной записи";
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }

    // Тест 2: Пропущено поле password
    @Test
    @Story("Validate error for missing required fields in courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a courier without a password returns an error")
    public void testCreateCourierWithoutPassword() {
        CourierData courierData = new CourierData(DEFAULT_LOGIN, null, DEFAULT_FIRST_NAME);
        Response response = courierApi.createCourier(courierData);

        // Проверяем код ответа 400 для ошибки
        courierApi.checkStatusCode(response, 400);

        // Ожидаемое сообщение об ошибке
        String expectedMessage = "Недостаточно данных для создания учетной записи";
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }

    // Тест 3: Пропущено поле firstName
    @Test
    @Story("Validate error for missing required fields in courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a courier without a first name returns an error")
    public void testCreateCourierWithoutFirstName() {
        CourierData courierData = new CourierData(DEFAULT_LOGIN, DEFAULT_PASSWORD, null);
        Response response = courierApi.createCourier(courierData);

        // Проверяем код ответа 400 для ошибки
        courierApi.checkStatusCode(response, 400);

        // Ожидаемое сообщение об ошибке
        String expectedMessage = "Недостаточно данных для создания учетной записи";
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }
}
