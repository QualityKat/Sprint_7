package couriertest.tests;

import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

public class ResponseValidator {

    public void checkStatusCode(Response response, int expectedStatusCode) {
        MatcherAssert.assertThat(response.getStatusCode(), Matchers.is(expectedStatusCode));
    }

    public void checkErrorMessage(Response response, String expectedMessage) {
        MatcherAssert.assertThat(response.jsonPath().getString("message"), Matchers.is(expectedMessage));
    }
}

