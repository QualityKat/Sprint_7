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

public class ResponseValidator {
    public class ResponseValidator {

        public void checkStatusCode(Response response, int expectedStatusCode) {
            assertThat(response.getStatusCode(), is(expectedStatusCode));
        }

        public void checkErrorMessage(Response response, String expectedMessage) {
            assertThat(response.jsonPath().getString("message"), is(expectedMessage));
        }
    }