package pe.interbank.bfa.front.steps.identityAuth;

import io.cucumber.core.exception.CucumberException;
import io.restassured.http.ContentType;
import net.serenitybdd.core.Serenity;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import pe.interbank.bfa.front.configuration.OpbkConfiguration;
import pe.interbank.bfa.front.steps.identity.IdentityEnrollmentSteps;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static io.restassured.RestAssured.given;
import static pe.interbank.bfa.front.utils.Constants.*;

public class IdentityNewAuthenticationSteps {
    public static final String TEMPLATES_PATH = "/src/main/resources/templates/";
    public static final String CHANNEL_JSON = "channel";
    public static final String APPLICATION_JSON = "application";
    public int currentFailedAttemptsBefore;
    public String messageError;
    public String codeError;

    IdentityEnrollmentSteps objIdentityEnrollmentSteps = new IdentityEnrollmentSteps();


    public void request2CustomerEnrollmentStatusLock(String documentNumber) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV).toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV).toString();
        try {
            var clientStatusLock = getCustomerEnrollment2StatusLock(documentNumber, token, channel);
            if (clientStatusLock) fiendClient2ToUnlock(documentNumber);
            System.out.println("ENtro al If - clientStatusLock = " + clientStatusLock);
        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }

    }

    private void fiendClient2ToUnlock(String documentNumber) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV).toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV).toString();
        given()
                .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentity2SubscriptionKey())
                .header(AUTHORIZATION_HEADER, token)
                .header(CORRELATION_ID_HEADER, "toUnlock" + Instant.now().getEpochSecond())
                .queryParam(DOCUMENT_TYPE_PARAM, DOCUMENT_TYPE_DNI)
                .queryParam(DOCUMENT_NUMBER_PARAM, documentNumber)
                .queryParam(COMPANY_PARAM, COMPANY_INTERBANK)
                .queryParam(CHANNEL_PARAM, channel)
                .queryParam("operationType", "ENROLL")
                .log().all()
                .put(OpbkConfiguration.getOpbkBaseUrl() + OpbkConfiguration.getUrlAuth2() + "customer/unlock")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_NO_CONTENT)
        ;
    }

    private boolean getCustomerEnrollment2StatusLock(String documentNumber, String token, String channel) {
        var responseBody = given()
                .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentity2SubscriptionKey())
                .header(AUTHORIZATION_HEADER, token)
                .queryParam(DOCUMENT_TYPE_PARAM, DOCUMENT_TYPE_DNI)
                .queryParam(DOCUMENT_NUMBER_PARAM, documentNumber)
                .queryParam(CHANNEL_PARAM, channel)
                .queryParam(COMPANY_PARAM, COMPANY_INTERBANK)
                .log().all()
                .get(OpbkConfiguration.getOpbkBaseUrl() + OpbkConfiguration.getUrlAuth2() + "customer")
                .then()
                .log().all()
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

    public void requestCustomerNewAuthenticationStatusLock(String documentNumber) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        try {
            var clientStatusLock = get2CustomerAuthenticationStatusLock(documentNumber, token, channel);
            if (clientStatusLock) fiendClientToUnlockAuthentication(documentNumber);

        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    private void fiendClientToUnlockAuthentication(String documentNumber) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV).toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV).toString();
        given()
                .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentity2SubscriptionKey())
                .header(AUTHORIZATION_HEADER, token)
                .header(CORRELATION_ID_HEADER, "toUnlock" + Instant.now().getEpochSecond())
                .queryParam(DOCUMENT_TYPE_PARAM, DOCUMENT_TYPE_DNI)
                .queryParam(DOCUMENT_NUMBER_PARAM, documentNumber)
                .queryParam(COMPANY_PARAM, COMPANY_INTERBANK)
                .queryParam(CHANNEL_PARAM, channel)
                .queryParam("operationType", "AUTHENTICATION")
                .log().all()
                .put(OpbkConfiguration.getOpbkBaseUrl() + OpbkConfiguration.getUrlAuth2() + "customer/unlock")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_NO_CONTENT)
        ;
    }

    private boolean get2CustomerAuthenticationStatusLock(String documentNumber, String token, String channel) {
        var responseBody = given()
                .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentity2SubscriptionKey())
                .header(AUTHORIZATION_HEADER, token)
                .queryParam(DOCUMENT_TYPE_PARAM, DOCUMENT_TYPE_DNI)
                .queryParam(DOCUMENT_NUMBER_PARAM, documentNumber)
                .queryParam(CHANNEL_PARAM, channel)
                .queryParam(COMPANY_PARAM, COMPANY_INTERBANK)
                .get(OpbkConfiguration.getOpbkBaseUrl() + OpbkConfiguration.getUrlAuth2() + "customer")
                .then()
                .log()
                .all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath();
        var blockStatus = responseBody.getBoolean("authenticateTrace.block");
        if (blockStatus) {
            Serenity.setSessionVariable(CURRENT_FAILED_ATTEMPTS).to("0");
        } else {
            var currentFailedAttempts = responseBody.getString("authenticateTrace.failedAttempts");
            Serenity.setSessionVariable(CURRENT_FAILED_ATTEMPTS).to(currentFailedAttempts);
        }
        return blockStatus;
    }

    public void requestClientNewAuthentication(String documentNumber, Integer status, String msg) {

        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;

        try {
            String statusAuth = "Positive";

            body = loadAuthemticateRequest2BodyOk(documentNumber, channel, statusAuth);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentity2SubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "newAut" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .log().all()
                    .post(OpbkConfiguration.getOpbkBaseUrl() + OpbkConfiguration.getUrlAuth2() + "user")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .log().all()
                    .extract()
                    .response()
                    .jsonPath();

            var facialStatus = response.getString("authentication.user.facial.status");
            objIdentityEnrollmentSteps.setFacialStatus(facialStatus);
            var facialDescription = response.getString("authentication.user.facial.description");
            objIdentityEnrollmentSteps.setFacialDescription(facialDescription);

        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    private JSONObject loadAuthemticateRequest2BodyOk(String documentNumber, String channel, String statusAuth) throws Exception {
        var path = System.getProperty("user.dir") + TEMPLATES_PATH + "identity-authenticate-" + documentNumber + "-" + statusAuth + ".json";
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

    public void getValidateMessageNewAuthentication(String documentNumber, String statusAuth, String descripcion) {
        if (statusAuth.equals(objIdentityEnrollmentSteps.getFacialStatus()) && descripcion.equals(objIdentityEnrollmentSteps.getFacialDescription())) {
            System.out.println("*** Verificando mensaje y código de error ***");
            System.out.println("statusAuth: " + objIdentityEnrollmentSteps.getFacialStatus());
            System.out.println("descripcion: " + objIdentityEnrollmentSteps.getFacialDescription());
        } else {
            throw new CucumberException("No paso la validación");
        }
    }

    public void requestClientNewAuthenticationError1(String documentNumber, Integer status, String msg) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;
        try {
            codeError = "130104";
            body = loadAuthenticateRequestBodyError(documentNumber, channel, codeError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentity2SubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "newAut2" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .log().all()
                    .post(OpbkConfiguration.getOpbkBaseUrl() + OpbkConfiguration.getUrlAuth2() + "user")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .log().all()
                    .extract()
                    .response()
                    .jsonPath();

            var messageError = response.getString("error.message");
            objIdentityEnrollmentSteps.setMessageError(messageError);
            var codError = response.getString("error.code");
            objIdentityEnrollmentSteps.setCodeError(codError);

        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    private JSONObject loadAuthenticateRequestBodyError(String documentNumber, String channel, String codigoError) throws Exception {
        var path = System.getProperty("user.dir") + TEMPLATES_PATH + "identity-authenticate-" + documentNumber + "-" + codigoError + "-error" + ".json";
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

    public void getValidateErrorCodeMessage(String documentNumber, String errorCode, String errorMessage, Integer status) {
        if (errorCode.equals(objIdentityEnrollmentSteps.getCodeError()) && errorMessage.equals(objIdentityEnrollmentSteps.getMessageError())) {

            System.out.println("*** Verificando mensaje y código de error ***");
            System.out.println("messageError: " + objIdentityEnrollmentSteps.getMessageError());
            System.out.println("codeError: " + objIdentityEnrollmentSteps.getCodeError());

        } else {
            throw new CucumberException("No paso la validación");
        }
    }

    public void requestClientNewAuthenticationError2(String documentNumber, Integer status, String msg) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;
        try {
            codeError = "130217";
            body = loadAuthenticateRequestBodyError(documentNumber, channel, codeError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentity2SubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "newAut4" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .log().all()
                    .post(OpbkConfiguration.getOpbkBaseUrl() + OpbkConfiguration.getUrlAuth2() + "user")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .log().all()
                    .extract()
                    .response()
                    .jsonPath();

            var messageError = response.getString("error.message");
            objIdentityEnrollmentSteps.setMessageError(messageError);
            var codError = response.getString("error.code");
            objIdentityEnrollmentSteps.setCodeError(codError);
        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    public void requestClientNewAuthenticationError3(String documentNumber, Integer status, String msg) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;
        try {
            codeError = "130213";
            body = loadAuthenticateRequestBodyError(documentNumber, channel, codeError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentity2SubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "newtAut3" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .log().all()
                    .post(OpbkConfiguration.getOpbkBaseUrl() + OpbkConfiguration.getUrlAuth2() + "user")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .log().all()
                    .extract()
                    .response()
                    .jsonPath();

            var messageError = response.getString("error.message");
            objIdentityEnrollmentSteps.setMessageError(messageError);
            var codError = response.getString("error.code");
            objIdentityEnrollmentSteps.setCodeError(codError);
        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    public void requestClientNewAuthenticationError4(String documentNumber, Integer status, String msg) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;
        try {
            codeError = "130217";
            body = loadAuthenticateRequestBodyError(documentNumber, channel, codeError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentity2SubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "newtAut4" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .log().all()
                    .post(OpbkConfiguration.getOpbkBaseUrl() + OpbkConfiguration.getUrlAuth2() + "user")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .log().all()
                    .extract()
                    .response()
                    .jsonPath();

            var messageError = response.getString("error.message");
            objIdentityEnrollmentSteps.setMessageError(messageError);
            var codError = response.getString("error.code");
            objIdentityEnrollmentSteps.setCodeError(codError);
        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    public void requestClientNewAuthenticationError5(String documentNumber, Integer status, String msg) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;
        try {
            codeError = "130214";
            body = loadAuthenticateRequestBodyError(documentNumber, channel, codeError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentity2SubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "newtAut5" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .log().all()
                    .post(OpbkConfiguration.getOpbkBaseUrl() + OpbkConfiguration.getUrlAuth2() + "user")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .log().all()
                    .extract()
                    .response()
                    .jsonPath();

            var messageError = response.getString("error.message");
            objIdentityEnrollmentSteps.setMessageError(messageError);
            var codError = response.getString("error.code");
            objIdentityEnrollmentSteps.setCodeError(codError);
        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    public void requestClientNewAuthenticationError6(String documentNumber, Integer status, String msg) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;
        try {
            codeError = "130216";
            body = loadAuthenticateRequestBodyError(documentNumber, channel, codeError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentity2SubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "newtAut6" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .log().all()
                    .post(OpbkConfiguration.getOpbkBaseUrl() + OpbkConfiguration.getUrlAuth2() + "user")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .log().all()
                    .extract()
                    .response()
                    .jsonPath();

            var messageError = response.getString("error.message");
            objIdentityEnrollmentSteps.setMessageError(messageError);
            var codError = response.getString("error.code");
            objIdentityEnrollmentSteps.setCodeError(codError);
        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    public void requestClientNewAuthenticationError7(String documentNumber, Integer status, String msg) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;
        try {
            codeError = "130218";
            body = loadAuthenticateRequestBodyError(documentNumber, channel, codeError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentity2SubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "newtAut6" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .log().all()
                    .post(OpbkConfiguration.getOpbkBaseUrl() + OpbkConfiguration.getUrlAuth2() + "user")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .log().all()
                    .extract()
                    .response()
                    .jsonPath();

            var messageError = response.getString("error.message");
            objIdentityEnrollmentSteps.setMessageError(messageError);
            var codError = response.getString("error.code");
            objIdentityEnrollmentSteps.setCodeError(codError);
        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    public void requestClientNewAuthenticationError8(String documentNumber, Integer status, String msg) {

        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;
        try {
            codeError = "130219";
            body = loadAuthenticateRequestBodyError(documentNumber, channel, codeError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentity2SubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "newtAut6" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .log().all()
                    .post(OpbkConfiguration.getOpbkBaseUrl() + OpbkConfiguration.getUrlAuth2() + "user")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .log().all()
                    .extract()
                    .response()
                    .jsonPath();

            var messageError = response.getString("error.message");
            objIdentityEnrollmentSteps.setMessageError(messageError);
            var codError = response.getString("error.code");
            objIdentityEnrollmentSteps.setCodeError(codError);
        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }
}
