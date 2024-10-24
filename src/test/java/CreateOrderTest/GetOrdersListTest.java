package CreateOrderTest;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

//Тест с отдельно созданным классом методов (как один из вариантов)
public class GetOrdersListTest {

    @Test
    @DisplayName("Get order list")
    public void getAllOrders() {
        Response response = OrderClient.getAllOrders();
        response.then().assertThat().body("orders", hasSize(greaterThan(0))).and().statusCode(SC_OK);
    }
}
