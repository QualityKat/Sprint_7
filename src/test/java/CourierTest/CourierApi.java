package CourierTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

public class CourierApi {
    // Метод отправки POST запроса для создания курьера
    @Step("create courier")
    public Response createCourier(CourierData courierData) {
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(courierData.toJson())
                .when()
                .post("/api/v1/courier");
    }

    // Метод для авторизации
    public int authorizeCourier(CourierData courierData) {
        return getCourierId(courierData);
    }

    // Метод для получения ID курьера по логину и паролю
    private int getCourierId(CourierData courierData) {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(courierData.toJson())
                .when()
                .post("/api/v1/courier/login");

        if (response.getStatusCode() == 200) {
            return response.jsonPath().getInt("id");
        } else {
            return -1; // Если авторизация не удалась
        }
    }

    // Метод для удаления курьера по его ID
    public void deleteCourier(int courierId) {
        RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .delete("/api/v1/courier/" + courierId)
                .then()
                .statusCode(200); // Ожидаем успешное удаление курьера
    }

    // Метод для вывода кода и тела ответа
    public void printResponse(Response response) {
        System.out.println("Код ответа: " + response.getStatusCode());
        System.out.println("Тело ответа: " + response.getBody().asString());
    }
}
