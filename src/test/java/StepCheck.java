import io.qameta.allure.Step;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.*;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class StepCheck {

    @Step("Проверка статус-кода")
    public void checkResponseStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        assertEquals(expectedStatusCode, actualStatusCode, "Статус-код не совпадает с ожидаемым.");
    }

    @Step("Проверка поля ok")
    public void checkResponseOk(Response response, boolean expectedOk) {
        boolean actualOk = response.jsonPath().getBoolean("ok");
        assertEquals(expectedOk, actualOk, "\"ok\" не совпадает с ожидаемым значением.");
    }

    @Step("Проверка поля {expectedField} на null")
    public void checkResponseFieldNotNull(Response response, String expectedField) {
        response.then().assertThat().body(expectedField, notNullValue());
    }

    @Step("Проверка ожидаемого значения {expectedValue} поля {fieldName}")
    public void checkResponseField(Response response, String fieldName, Object expectedValue) {
        response.then().assertThat().body(fieldName, equalTo(expectedValue));
    }

    @Step("Проверка наличия поля {expectedField}")
    public void checkResponseFieldExists(Response response, String expectedField) {
        response.then().assertThat().body("$", hasKey(expectedField));
    }

    @Step("Проверка поля {expectedField} на не пустое значение")
    public void checkResponseFieldNotEmpty(Response response, String expectedField) {
        response.then().assertThat()
                .body(expectedField, is(not(emptyOrNullString())));
    }

    @Step("Проверка массива {expectedArray} на не пустое значение")
    public void checkResponseArrayNotEmpty(Response response, String expectedArray) {
        response.then().assertThat()
                .body(expectedArray, is(not(emptyArray())));
    }

    @Step("Проверка массива {arrayPath} на наличие ключа {expectedField} и на непустое значение")
    public void checkFieldExistsInArray(Response response, String arrayPath, String expectedField) {
        response.then().assertThat()
                .body(arrayPath + "." + expectedField, everyItem(notNullValue()));
    }

}


