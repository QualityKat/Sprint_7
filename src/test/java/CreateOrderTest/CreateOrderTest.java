package CreateOrderTest;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.apache.http.HttpStatus.*;

//Тесты с созданием отдельного класса с методами (как один из вариантов)

@RunWith(Parameterized.class)
public class CreateOrderTest {
    private String firstName;
    private String lastName;
    private String address;
    private String metroStation;
    private String phone;
    private String deliveryDate;
    private String comment;
    private String[] color;
    private int rentTime;

    String orderId;

    @After
    public void tearDown() {
        // Удаление заказа по ID
        OrderClient.deleteOrder(orderId);
    }

    public CreateOrderTest(String firstName, String lastName, String address, String metroStation,
                           String phone, int rentTime, String deliveryDate, String comment, String[] color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] getOrderData() {
        return new Object[][]{
                { "lalala", "tututu", "SPb, 1 apt.", "22", "+7 800 333 23 23", 3, "2025-07-07", "Call before", new String[] { "GRAY" } },
                { "lalala", "tututu", "SPb, 1 apt.", "22", "+7 800 333 23 23", 3, "2025-07-07", "Call before", new String[] { "GRAY", "BLACK" } },
                { "lalala", "tututu", "SPb, 1 apt.", "22", "+7 800 333 23 23", 3, "2025-07-07", "Call before", new String[] { } },
                { "lalala", "tututu", "SPb, 1 apt.", "22", "+7 800 333 23 23", 3, "2025-07-07", "Call before", new String[] { "BLACK" } },
        };
    }

    //Тест на создание заказа самоката разных цветов и удаление заказа по id заказа
    //*Тест не проходит: не удается получить id заказа и удаление заказа по ручкам из документации = баг*
    //*Тест также не проходит если удалять заказ по трек-номеру (как в закомменченом коде ниже) = баг*
    @Test
    @DisplayName("Creating an order with different colors")
    public void createOrderParameterizedColorScooterTest() {
        // Создание объекта заказа
        OrderCreate orderCreate = new OrderCreate(firstName, lastName, address,
                metroStation, phone, deliveryDate, comment, color, rentTime);
        // Создание нового заказа
        Response createResponse = OrderClient.createNewOrder(orderCreate);
        // Проверка успешного создания заказа
        OrderClient.comparingSuccessfulOrderSet(createResponse, SC_CREATED);
        // Получение ID заказа
        orderId = OrderClient.getOrderId(createResponse);
        // Удаление заказа
        Response deleteResponse = OrderClient.deleteOrder(orderId);
        // Проверка успешного удаления заказа
        OrderClient.comparingSuccessfulOrderCancel(deleteResponse, SC_OK);
    }
}
