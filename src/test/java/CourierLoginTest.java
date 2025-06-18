import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.ThreadLocalRandom;

public class CourierLoginTest extends StepCheck {
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
    @DisplayName("Проверка авторизации курьера")
    public void testCourierLogin() {
        CourierCreationModel newCourier = new CourierCreationModel(courierLoginRandom, "1234", "saske");
        Response courier = courierApiClient.createNewCourier(newCourier);
        checkResponseStatusCode(courier, 201);
        checkResponseOk(courier, true);

        courierId = courierApiClient.loginCourierForCleanUp(newCourier.getLogin(), newCourier.getPassword());

        CourierLoginModel courierLoginModel = new CourierLoginModel(newCourier.getLogin(), newCourier.getPassword());
        Response courierLogin = courierApiClient.loginCourier(courierLoginModel);
        checkResponseStatusCode(courierLogin, 200);
        checkResponseFieldExists(courierLogin, "id");
        checkResponseFieldNotNull(courierLogin, "id");
        checkResponseFieldNotEmpty(courierLogin, "id");
    }

    static Object[][] provideInvalidCourierLoginData() {
        return new Object[][]{
                {null, "1234", "Недостаточно данных для входа"}, // Логин null
                {"login", null, "Недостаточно данных для входа"}, // Пароль null
                {null, null, "Недостаточно данных для входа"}    // Логин и Пароль null
        };
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCourierLoginData")
    @DisplayName("Проверка авторизации курьера без обязательных полей")
    public void testCourierLoginWithOutRequiredFields(String login, String password, String expectedMessage) {
        CourierCreationModel newCourier = new CourierCreationModel(courierLoginRandom, "1234", "saske");
        Response courier = courierApiClient.createNewCourier(newCourier);
        checkResponseStatusCode(courier, 201);
        checkResponseOk(courier, true);

        courierId = courierApiClient.loginCourierForCleanUp(newCourier.getLogin(), newCourier.getPassword());

        CourierLoginModel courierLoginModel = new CourierLoginModel(
                login == null || login.isBlank() ? null : newCourier.getLogin(),
                password == null || password.isBlank() ? null : newCourier.getPassword());
        Response courierLoginResponse = courierApiClient.loginCourier(courierLoginModel);

        checkResponseStatusCode(courierLoginResponse, 400);
        checkResponseField(courierLoginResponse, "message", expectedMessage);
    }

    @Test
    @DisplayName("Проверка авторизации курьера с несуществующим логином и паролем")
    public void testCourierLoginWithNonExistentCredentials() {
        String courierPasswordRandom = "1234" + ThreadLocalRandom.current().nextInt(100, 100000);

        CourierLoginModel courierLoginModel = new CourierLoginModel(courierLoginRandom, courierPasswordRandom);
        Response courierLogin = courierApiClient.loginCourier(courierLoginModel);
        checkResponseStatusCode(courierLogin, 404);
        checkResponseField(courierLogin, "code", 404);
        checkResponseField(courierLogin, "message", "Учетная запись не найдена");
    }

}
