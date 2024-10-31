package couriertest.api;
//  Алеся, я постаралась учесть все обязательные исправления, спасибо большое за гамотные наводки, это очень ценно
// Очень надеюсь что я на верном пути, хотя мозги уже запутались окончательно)))
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.qameta.allure.Step;

public class CourierApi {

    @Step("Create courier")
    public Response createCourier(CourierData courierData) {
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(courierData.toJson())
                .when()
                .post("/api/v1/courier");
    }

    @Step("Authorize courier")
    public int authorizeCourier(CourierData courierData) {
        return getCourierId(courierData);
    }

    public int getCourierId(CourierData courierData) {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(courierData.toJson())
                .when()
                .post("/api/v1/courier/login");

        if (response.getStatusCode() == 200) {
            return response.jsonPath().getInt("id");
        } else {
            return -1;
        }
    }

    @Step("Delete courier")
    public Response deleteCourier(int courierId) {
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .delete("/api/v1/courier/" + courierId);
    }

    //  printResponse метод удален, т.к.  наверное лучше использовать  ResponseValidator или логирование для вывода информации.
}
