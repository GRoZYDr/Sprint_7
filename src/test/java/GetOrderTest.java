import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

public class GetOrderTest extends StepCheck {
    OrderApiClient orderApiClient;

    @BeforeEach
    public void setUp() {
        orderApiClient = new OrderApiClient();
    }

    @Test
    @DisplayName("Проверка получения списка заказов")
    public void testGetOrderList() {
        Response response = orderApiClient.getOrder();
        checkResponseStatusCode(response, 200);
        checkResponseFieldExists(response, "orders");
        checkResponseArrayNotEmpty(response, "order");
        checkFieldExistsInArray(response, "orders", "id");
        checkFieldExistsInArray(response, "orders", "firstName");
        checkFieldExistsInArray(response, "orders", "lastName");
        checkFieldExistsInArray(response, "orders", "address");
        checkFieldExistsInArray(response, "orders", "metroStation");
        checkFieldExistsInArray(response, "orders", "phone");
        checkFieldExistsInArray(response, "orders", "rentTime");
        checkFieldExistsInArray(response, "orders", "deliveryDate");
        checkFieldExistsInArray(response, "orders", "track");
        checkFieldExistsInArray(response, "orders", "comment");
        checkFieldExistsInArray(response, "orders", "createdAt");
        checkFieldExistsInArray(response, "orders", "updatedAt");
        checkFieldExistsInArray(response, "orders", "status");
        checkResponseFieldExists(response, "pageInfo");
        checkResponseFieldNotNull(response, "pageInfo.page");
        checkResponseFieldNotNull(response, "pageInfo.total");
        checkResponseFieldNotNull(response, "pageInfo.limit");
        checkResponseFieldExists(response, "availableStations");
        checkResponseArrayNotEmpty(response, "availableStations");
        checkFieldExistsInArray(response, "availableStations", "name");
        checkFieldExistsInArray(response, "availableStations", "number");
        checkFieldExistsInArray(response, "availableStations", "color");
    }
}
