package pe.interbank.bfa.front.steps.identity;

import io.cucumber.core.exception.CucumberException;
import io.restassured.http.ContentType;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import pe.interbank.bfa.front.configuration.OpbkConfiguration;
import pe.interbank.bfa.front.utils.Security;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;

import org.json.JSONObject;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static pe.interbank.bfa.front.utils.Constants.*;

public class IdentityEnrollmentSteps {

    public static final String TEMPLATES_PATH = "/src/main/resources/templates/";
    public static final String CHANNEL_JSON = "channel";
    public static final String APPLICATION_JSON = "application";
    public int currentFailedAttemptsBefore;
    public String messageError;
    public String codeError;
    public String facialStatus;
    public String facialDescription;

    @Step("consultar si el cliente esta enrollado en el canal")
    public void getAccessTokenOPBK() {
        var token = Security.createAccessTokenOPBK();
        Serenity.setSessionVariable(OPBK_TOKEN_ENV)
                .to(token);
    }

    public void setChannel(String channel) {
        Serenity.setSessionVariable(CHANNEL_ENV)
                .to(channel);
    }

    public void requestClientEnrollmentStatus(String documentNumber, String status) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        try {
            var clientStatus = getCustomerEnrollmentStatus(documentNumber, token, channel);
            if (!status.equals(clientStatus)) findClientToUnenroll(documentNumber);
        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    private void findClientToUnenroll(String documentNumber) {
        try {
            var customerId = getCustomerId(documentNumber);
            unenrollClient(customerId);
        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    private String getCustomerId(String documentNumber) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var users = given()
                .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                .header(AUTHORIZATION_HEADER, token)
                .queryParam(DOCUMENT_TYPE_PARAM, DOCUMENT_TYPE_DNI)
                .queryParam(DOCUMENT_NUMBER_PARAM, documentNumber)
                .get(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/customer/enrolled")
                .then()
                .extract()
                .response()
                .jsonPath()
                .getList("users");
        if (users.isEmpty()) throw new CucumberException("No se encontraron enrolamientos");
        var userDetails = (HashMap<?, ?>) users.get(0);
        var details = (HashMap<?, ?>) userDetails.get("detail");
        return details.get("customerId").toString();
    }

    private void unenrollClient(String customerId) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        given()
                .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                .header(AUTHORIZATION_HEADER, token)
                .header(CORRELATION_ID_HEADER, "unenroll" + Instant.now().getEpochSecond())
                .header(UPDATED_BY_HEADER, "XT9415")
                .header(X_API_VERSION_HEADER, "1.0")
                .header(COMPANY_PARAM, COMPANY_INTERBANK)
                .header(CHANNEL_PARAM, channel)
                .pathParam(CUSTOMER_CODE, customerId)
                .put(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/customer/enrollment/{code}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    public void requestExtractOcr(String documentNumber) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;
        try {
            body = loadExtractOcrRequestBody(documentNumber, channel);
            var ocrKey = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testenr1" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/document/extract")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .extract()
                    .response()
                    .jsonPath()
                    .getString("ocrKey");
            Serenity.setSessionVariable(OCR_KEY_ENV)
                    .to(ocrKey);
        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    private JSONObject loadExtractOcrRequestBody(String documentNumber, String channel) throws Exception {
        var path = System.getProperty("user.dir") + TEMPLATES_PATH + "identity-extractocr-" + documentNumber + "-ok.json";
        var file = new File(path);
        if (file.exists()) {
            var fileStream = new FileInputStream(path);
            var jsonTxt = IOUtils.toString(fileStream, StandardCharsets.UTF_8);
            var json = new JSONObject(jsonTxt);
            var requestChannel = (JSONObject) json.get(CHANNEL_JSON);
            requestChannel.put(APPLICATION_JSON, channel);
            json.put(CHANNEL_JSON, requestChannel);
            return json;
        }
        throw new CucumberException("Archivo no encontrado");
    }

    public void requestClientEnrollment(String documentNumber) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        var ocrKey = Serenity.sessionVariableCalled("OCR_KEY")
                .toString();
        JSONObject body;
        try {
            body = loadEnrollmentRequestBody(ocrKey, documentNumber, channel);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testenr2" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .header(X_USER_RENIEC_HEADER, "DTBUSR")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/web-enrollment")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .extract()
                    .response()
                    .jsonPath();
        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    private JSONObject loadEnrollmentRequestBody(String ocrKey, String documentNumber, String channel) throws Exception {
        var path = System.getProperty("user.dir") + TEMPLATES_PATH + "identity-enrollment-" + documentNumber + "-ok.json";
        var file = new File(path);
        if (file.exists()) {
            var fileStream = new FileInputStream(path);
            var jsonTxt = IOUtils.toString(fileStream, StandardCharsets.UTF_8);
            var json = new JSONObject(jsonTxt);

            var user = (JSONObject) json.get("user");
            var ocr = (JSONObject) user.get("ocr");
            ocr.put("ocrKey", ocrKey);
            user.put("ocr", ocr);
            json.put("user", user);

            var requestChannel = (JSONObject) json.get(CHANNEL_JSON);
            requestChannel.put(APPLICATION_JSON, channel);
            json.put(CHANNEL_JSON, requestChannel);

            return json;
        }
        throw new CucumberException("Archivo no encontrado");
    }

    public void validateEnrolledCustomer(String documentNumber) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        try {
            var clientStatus = getCustomerEnrollmentStatus(documentNumber, token, channel);
            if (!clientStatus.equals("ENROLLED")) throw new CucumberException("El cliente no esta enrrolado");
        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    public static String getCustomerEnrollmentStatus(String documentNumber, String token, String channel) {
        return given()
                .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                .header(AUTHORIZATION_HEADER, token)
                .queryParam(DOCUMENT_TYPE_PARAM, DOCUMENT_TYPE_DNI)
                .queryParam(DOCUMENT_NUMBER_PARAM, documentNumber)
                .queryParam(CHANNEL_PARAM, channel)
                .queryParam(COMPANY_PARAM, COMPANY_INTERBANK)
                .get(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/customer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath()
                .getString("status")

                ;

    }

    public void requestCustomerEnrollmentStatusLock(String documentNumber) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        try {

            var clientStatusLock = getCustomerEnrollmentStatusLock(documentNumber, token, channel);
            if (clientStatusLock) fiendClientToUnlock(documentNumber);
        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    public void fiendClientToUnlock(String documentNumber) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV).toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV).toString();
        given()
                .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                .header(AUTHORIZATION_HEADER, token)
                .header(CORRELATION_ID_HEADER, "toUnlock" + Instant.now().getEpochSecond())
                .queryParam(DOCUMENT_TYPE_PARAM, DOCUMENT_TYPE_DNI)
                .queryParam(DOCUMENT_NUMBER_PARAM, documentNumber)
                .queryParam(COMPANY_PARAM, COMPANY_INTERBANK)
                .queryParam(CHANNEL_PARAM, channel)
                .queryParam("operationType", "ENROLL")
                .log().all()
                .put(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/customer/unlock")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_NO_CONTENT)
        ;
    }

    public boolean getCustomerEnrollmentStatusLock(String documentNumber, String token, String channel) {
        var responseBody = given()
                .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                .header(AUTHORIZATION_HEADER, token)
                .queryParam(DOCUMENT_TYPE_PARAM, DOCUMENT_TYPE_DNI)
                .queryParam(DOCUMENT_NUMBER_PARAM, documentNumber)
                .queryParam(CHANNEL_PARAM, channel)
                .queryParam(COMPANY_PARAM, COMPANY_INTERBANK)
                .get(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/customer")
                .then()
                .log()
                .all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath();
        var blockStatus = responseBody.getBoolean("enrollmentTrace.block");
        if (blockStatus) {
            Serenity.setSessionVariable(CURRENT_FAILED_ATTEMPTS).to("0");
        } else {
            var currentFailedAttempts = responseBody.getString("enrollmentTrace.failedAttempts");
            Serenity.setSessionVariable(CURRENT_FAILED_ATTEMPTS).to(currentFailedAttempts);
        }
        return blockStatus;
    }

    public void requestClientEnrollmentTraditional(String documentNumber, Integer status) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV).toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV).toString();
        JSONObject body;

        try {
            body = requestClientEnrollmentTraditional(documentNumber, channel);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testenr3" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .header(X_USER_RENIEC_HEADER, "DTBUSR")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .log().all()
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/enrollment")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .log().all()
                    .extract()
                    .response()
                    .jsonPath();
        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }

    }

    private JSONObject requestClientEnrollmentTraditional(String documentNumber, String channel) throws Exception {
        var path = System.getProperty("user.dir") + TEMPLATES_PATH + "identity-enrollment-tradicional-" + documentNumber + "-ok.json";
        var file = new File(path);
        if (file.exists()) {
            var fileStream = new FileInputStream(path);
            var jsonTxt = IOUtils.toString(fileStream, StandardCharsets.UTF_8);
            var json = new JSONObject(jsonTxt);
            var requestChannel = (JSONObject) json.get(CHANNEL_JSON);
            requestChannel.put(APPLICATION_JSON, channel);
            json.put(CHANNEL_JSON, requestChannel);
            return json;
        }
        throw new CucumberException("Archivo no encontrado");
    }

    public void requestClientEnrollmentTraditionalError(String documentNumber, Integer status) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV).toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV).toString();
        JSONObject body;

        try {
            body = requestClientEnrollmentTraditionalError(documentNumber, channel);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "130204" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .header(X_USER_RENIEC_HEADER, "DTBUSR")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .log().all()
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/enrollment")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .log().all()
                    .extract()
                    .response()
                    .jsonPath();
        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }

    }

    private JSONObject requestClientEnrollmentTraditionalError(String documentNumber, String channel) throws Exception {
        var path = System.getProperty("user.dir") + TEMPLATES_PATH + "identity-enrollment-tradicional-" + documentNumber + "-130204-error.json";
        var file = new File(path);
        if (file.exists()) {
            var fileStream = new FileInputStream(path);
            var jsonTxt = IOUtils.toString(fileStream, StandardCharsets.UTF_8);
            var json = new JSONObject(jsonTxt);
            var requestChannel = (JSONObject) json.get(CHANNEL_JSON);
            requestChannel.put(APPLICATION_JSON, channel);
            json.put(CHANNEL_JSON, requestChannel);
            return json;
        }
        throw new CucumberException("Archivo no encontrado");
    }

    public void getValidateErrorCodeMessageEnrollment(String documentNumber, String errorCode, String errorMessage, Integer status) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;

        try {
            body = requestClientEnrollmentTraditionalError(documentNumber, channel);
            var responseBody = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "130204" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .header(X_USER_RENIEC_HEADER, "DTBUSR")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/enrollment")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .body("error.code", Matchers.equalTo(errorCode))
                    .body("error.message", Matchers.equalTo(errorMessage))
                    .log().all();


        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }


    public void setCountFailedBefore(Integer currentFailedAttemptsBefore) {
        this.currentFailedAttemptsBefore = currentFailedAttemptsBefore;
    }

    public int getCountFailedBefore() {
        return currentFailedAttemptsBefore;
    }

    public void setMessageError(String messageError) {
        this.messageError = messageError;
    }

    public String getMessageError() {
        return messageError;
    }

    public void setFacialStatus(String facialStatus) {
        this.facialStatus = facialStatus;
    }

    public String getFacialStatus() {
        return facialStatus;
    }

    public void setCodeError(String codeError) {
        this.codeError = codeError;
    }

    public String getCodeError() {
        return codeError;
    }

    public void setFacialDescription(String facialDescription) {
        this.facialDescription = facialDescription;
    }

    public String getFacialDescription() {
        return facialDescription;
    }
}
