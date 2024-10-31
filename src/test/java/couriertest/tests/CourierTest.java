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

public class CourierTest {

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
        CourierData courierData = new CourierData("qazhof", "1234", "saske");

        // Отправляем запрос на создание курьера
        Response response = courierApi.createCourier(courierData);

        // Проверяем код ответа
        assertThat(response.getStatusCode(), is(201));
        assertThat(response.jsonPath().get("ok"), is(true));

        // Авторизуемся, чтобы получить ID курьера
        courierId = courierApi.getCourierId(courierData); // Сохраняем ID курьера

        // Проверяем ID курьера получен корректно
        assertThat(courierId, is(not(-1))); // Убедитесь, что ID не -1
    }

    // Тест, что нельзя создать двух одинаковых курьеров
    @Test
    @Story("Prevent duplicate courier creation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that creating a courier with the same login returns an error")
    public void testErrorCreateTheSameCourier() {
        CourierData courierData = new CourierData("qazhof", "1234", "saske");

        // Отправляем первый запрос для создания курьера
        Response firstResponse = courierApi.createCourier(courierData);
        assertThat(firstResponse.getStatusCode(), is(201));

        // Отправляем второй запрос для создания того же курьера
        Response secondResponse = courierApi.createCourier(courierData);
        assertThat(secondResponse.getStatusCode(), is(409)); // Ожидаем ошибку 409

        // Ожидаемое сообщение об ошибке
        String expectedMessage = "Этот логин уже используется. Попробуйте другой.";
        assertThat(secondResponse.jsonPath().getString("message"), is(expectedMessage));

        // Авторизуемся, чтобы получить ID курьера
        courierId = courierApi.getCourierId(courierData); // Сохраняем ID курьера
        assertThat(courierId, is(not(-1))); // Убедитесь, что ID не -1
    }

    // Тест, чтобы создать курьера, нужно передать в ручку все обязательные поля
    @Test
    @Story("Validate required fields for courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that all required fields are present when creating a courier")
    public void testCreateCourierWithAllRequiredFields() {
        CourierData courierData = new CourierData("qazhof", "1234", "saske");

        // Отправляем POST запрос
        Response response = courierApi.createCourier(courierData);

        // Проверяем код ответа
        assertThat(response.getStatusCode(), is(201));
        assertThat(response.jsonPath().get("ok"), is(true));
    }

    // Тест на запрос создание курьера, возвращает правильный код ответа
    @Test
    @Story("Validate status code 201 for successful courier creation")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify that creating a new courier returns status code 201")
    public void testCreateCourierCode201() {
        CourierData courierData = new CourierData("qazhof", "1234", "saske");

        Response response = courierApi.createCourier(courierData);
        assertThat(response.getStatusCode(), is(201));

        // Авторизуемся, чтобы получить ID курьера
        courierId = courierApi.getCourierId(courierData); // Сохраняем ID курьера
        assertThat(courierId, is(not(-1))); // Убедитесь, что ID не -1
    }

    // Тест на успешный запрос создания курьера, возвращает ok: true
    @Test
    @Story("Validate 'ok: true' for successful courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a successful courier creation returns 'ok: true' in the response")
    public void testCreateCourierOkTrue() {
        CourierData courierData = new CourierData("qazhof", "1234", "saske");

        Response response = courierApi.createCourier(courierData);
        assertThat(response.jsonPath().get("ok"), is(true));

        // Авторизуемся, чтобы получить ID курьера
        courierId = courierApi.getCourierId(courierData); // Сохраняем ID курьера
        assertThat(courierId, is(not(-1))); // Убедитесь, что ID не -1
    }

    // Тест если одного из полей нет, запрос возвращает ошибку
    // Тест 1: Пропущено поле login
    @Test
    @Story("Validate error for missing required fields in courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a courier without a login returns an error")
    public void testCreateCourierWithoutLogin() {
        CourierData courierData = new CourierData(null, "1234", "saske"); // Пропускаем логин

        Response response = courierApi.createCourier(courierData);
        assertThat(response.getStatusCode(), is(400));
        assertThat(response.jsonPath().getString("message"), is("Недостаточно данных для создания учетной записи"));
    }

    // Тест 2: Пропущено поле password
    @Test
    @Story("Validate error for missing required fields in courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a courier without a password returns an error")
    public void testCreateCourierWithoutPassword() {
        CourierData courierData = new CourierData("qazhof", null, "saske"); // Пропускаем пароль

        Response response = courierApi.createCourier(courierData);
        assertThat(response.getStatusCode(), is(400));
        assertThat(response.jsonPath().getString("message"), is("Недостаточно данных для создания учетной записи"));
    }

    // Тест 3: Пропущено поле firstName
    @Test
    @Story("Validate error for missing required fields in courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a courier without a first name returns an error")
    public void testCreateCourierWithoutFirstName() {
        CourierData courierData = new CourierData("qazhof", "1234", null); // Пропускаем имя

        Response response = courierApi.createCourier(courierData);
        assertThat(response.getStatusCode(), is(400));
        assertThat(response.jsonPath().getString("message"), is("Недостаточно данных для создания учетной записи"));
    }
}
