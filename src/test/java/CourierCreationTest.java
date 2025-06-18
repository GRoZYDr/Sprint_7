import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.ThreadLocalRandom;

public class CourierCreationTest extends StepCheck {
    String courierLoginRandom;
    CourierApiClient courierApiClient;
    private Integer courierId;

    @BeforeEach
    public void setUp() {
        courierLoginRandom = "test_user" + ThreadLocalRandom.current().nextInt(100, 100000);
        courierApiClient = new CourierApiClient();
        courierId = null;
    }

    @AfterEach
    public void tearDown() {
        if (courierId != null) {
            courierApiClient.deleteCourier(courierId);
            courierId = null;
        }
    }

    @Test
    @DisplayName("Проверка создания курьера")
    public void testCourierCanBeCreated() {
        CourierCreationModel newCourier = new CourierCreationModel(courierLoginRandom, "1234", "saske");
        Response courier = courierApiClient.createNewCourier(newCourier);
        checkResponseStatusCode(courier, 201);
        checkResponseOk(courier, true);

        courierId = courierApiClient.loginCourierForCleanUp(newCourier.getLogin(), newCourier.getPassword());
    }


    @Test
    @DisplayName("Проверка создания двух одинаковых курьеров")
    public void testDuplicationCourierCreated() {
        String dupPassword = "1234";
        String dupFirstName = "saske";

        CourierCreationModel newCourier = new CourierCreationModel(courierLoginRandom, dupPassword, dupFirstName);

        Response courierFirst = courierApiClient.createNewCourier(newCourier);
        checkResponseStatusCode(courierFirst, 201);
        checkResponseOk(courierFirst, true);

        courierId = courierApiClient.loginCourierForCleanUp(newCourier.getLogin(), newCourier.getPassword());

        Response courierSecond = courierApiClient.createNewCourier(newCourier);
        checkResponseField(courierSecond, "code", 409);
        checkResponseField(courierSecond, "message", "Этот логин уже используется");


    }

    static Object[][] provideInvalidCourierData() {
        return new Object[][]{
                {null, "1234", "saske"}, // Логин null
                {"login", null, "saske"}, // Пароль null
                {"test_user" + ThreadLocalRandom.current().nextInt(100, 100000), "1234", null}    // Имя null
        };
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCourierData")
    @DisplayName("Проверка создания курьера при отсутствии обязательных полей")
    public void testCourierCreatedWhenRequiredFieldsMissing(String login, String password, String firstName) {
        CourierCreationModel newCourier = new CourierCreationModel(login, password, firstName);
        Response courier = courierApiClient.createNewCourier(newCourier);
        if (login != null && password != null && courier.getStatusCode() == 201) {
            courierId = courierApiClient.loginCourierForCleanUp(login, password);
        }
        checkResponseField(courier, "message", "Недостаточно данных для создания учетной записи");
    }
}



