package pe.interbank.bfa.front.steps.identity;

import io.cucumber.core.exception.CucumberException;
import io.restassured.http.ContentType;
import net.serenitybdd.core.Serenity;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import pe.interbank.bfa.front.configuration.OpbkConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static io.restassured.RestAssured.given;
import static pe.interbank.bfa.front.utils.Constants.*;

public class IdentityExpressEnrollmentSteps {

    public static final String TEMPLATES_PATH = "/src/main/resources/templates/";
    public static final String CHANNEL_JSON = "channel";
    public static final String APPLICATION_JSON = "application";
    private String codigoError;


    IdentityEnrollmentSteps objIdentityEnrollmentSteps= new IdentityEnrollmentSteps();

    public void requestClientExpressEnrollment(String numeroDocumento)  {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;

        try {
            body = loadExpressEnrollmentRequestBody(numeroDocumento, channel);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testexp1" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .header(X_USER_RENIEC_HEADER, "DTBUSR")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/express-enrollment")
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



    private JSONObject loadExpressEnrollmentRequestBody(String documentNumber, String channel) throws Exception {
        var path = System.getProperty("user.dir") + TEMPLATES_PATH + "identity-enrollment-express-" + documentNumber + "-ok.json";
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


    public int requestValidateUnlockCustomer(String documentNumber) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();

        try{
            var clientStatusLock =   objIdentityEnrollmentSteps.getCustomerEnrollmentStatusLock(documentNumber, token, channel);
            if (clientStatusLock) objIdentityEnrollmentSteps.fiendClientToUnlock(documentNumber);
            var countFailedAttemptsBefores = getCountFailedAttemptsBefore(documentNumber, token, channel);
            objIdentityEnrollmentSteps.setCountFailedBefore(countFailedAttemptsBefores);
            return objIdentityEnrollmentSteps.getCountFailedBefore();
        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    public int getCountFailedAttemptsBefore(String documentNumber, String token, String channel) {

        var responseBody = given()
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
                .log().all()
                .extract()
                .response()
                .jsonPath();

        var currentFailedAttemptsBefore1 = responseBody.getInt("enrollmentTrace.failedAttempts");
        objIdentityEnrollmentSteps.setCountFailedBefore(currentFailedAttemptsBefore1);
        return objIdentityEnrollmentSteps.getCountFailedBefore();

    }

    private JSONObject loadExpressEnrollmentRequestBodyError(String documentNumber, String channel, String codigoError) throws Exception {
        var path = System.getProperty("user.dir") + TEMPLATES_PATH + "identity-enrollment-express-" + documentNumber +"-" + codigoError +"-error"+".json";
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

    private JSONObject loadExpressEnrollmentRequestBodyErrorMask(String documentNumber, String channel, String codigoError) throws Exception {
        var path = System.getProperty("user.dir") + TEMPLATES_PATH + "identity-enrollment-express-" + documentNumber +"-" + codigoError +"-error-msk"+".json";
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

    private JSONObject loadExpressEnrollmentRequestBodyError5(String documentNumber, String channel, String codigoError) throws Exception {
        var path = System.getProperty("user.dir") + TEMPLATES_PATH + "identity-enrollment-express-" + documentNumber +"-" + codigoError +"-error"+".json";
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

    public void validateExpressEnrolledCustomer(String documentNumber) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        try {
            var clientStatus = getCustomerExpressEnrollmentStatus(documentNumber, token, channel);
            if (!clientStatus.equals("NOT_ENROLLED")) throw new CucumberException("ENROLLED");
        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    public static String getCustomerExpressEnrollmentStatus(String documentNumber, String token, String channel) {
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
                .getString("status");
    }

    public void getValidateErrorCodeMessage(String documentNumber, String errorCode, String errorMessage, Integer status) {

        if (errorCode.equals(objIdentityEnrollmentSteps.getCodeError()) && errorMessage.equals(objIdentityEnrollmentSteps.getMessageError())){

            System.out.println("*** Verificando mensaje y código de error ***");
            System.out.println("messageError: " + objIdentityEnrollmentSteps.getMessageError());
            System.out.println("codeError: " + objIdentityEnrollmentSteps.getCodeError());

        }else {throw new CucumberException("No paso la validación");}
    }

    public void request_Validate_Count_blocked_not_increment(String documentNumber) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        try {
            var countFailedAttemptsLater = getCountFailedAttemptsLater(documentNumber, token, channel);
            if (countFailedAttemptsLater == objIdentityEnrollmentSteps.getCountFailedBefore()) ;
            System.out.println("Se verifica que no aumenta la cantidad de intentos fallidos");
            System.out.println("countFailedAttempts-Antes: "+objIdentityEnrollmentSteps.getCountFailedBefore());
            System.out.println("countFailedAttempts-Después: "+countFailedAttemptsLater);
        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    public int getCountFailedAttemptsLater(String documentNumber, String token, String channel) {
        var responseBody = given()
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
                .log().all()
                .extract()
                .response()
                .jsonPath();
        var currentFailedAttemptsLater = responseBody.getInt("enrollmentTrace.failedAttempts");
        return currentFailedAttemptsLater;
    }



    public void requestValidateCountBlockIncrement(String documentNumber) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        try {
            var countFailedAttemptsLater = getCountFailedAttemptsLater(documentNumber, token, channel);
            if (countFailedAttemptsLater == objIdentityEnrollmentSteps.getCountFailedBefore()+1);
            System.out.println("*** Contador aumenta en 1 ***");
            System.out.println("countFailedAttempts - Antes: "+objIdentityEnrollmentSteps.getCountFailedBefore());
            System.out.println("countFailedAttempts - Después: "+countFailedAttemptsLater);

        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    public void requestClientExpressEnrollmentError1(String documentNumber, Integer status, String msg){
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;

        try {
            codigoError="130217";

            body = loadExpressEnrollmentRequestBodyErrorMask(documentNumber, channel, codigoError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testexp2" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .header(X_USER_RENIEC_HEADER, "DTBUSR")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/express-enrollment")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .log().all()
                    .extract()
                    .response()
                    .jsonPath();

            var messageError = response.getString("error.message");
            objIdentityEnrollmentSteps.setMessageError(messageError);
            var codError= response.getString("error.code");
            objIdentityEnrollmentSteps.setCodeError(codError);


        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }



    public void requestClientExpressEnrollmentError2(String documentNumber, Integer status, String msg){
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;

        try {
            codigoError="130104";

            body = loadExpressEnrollmentRequestBodyError(documentNumber, channel, codigoError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testexp3" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .header(X_USER_RENIEC_HEADER, "BIEUSR")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/express-enrollment")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .log().all()
                    .extract()
                    .response()
                    .jsonPath();

            var messageError = response.getString("error.message");
            objIdentityEnrollmentSteps.setMessageError(messageError);
            var codError= response.getString("error.code");
            objIdentityEnrollmentSteps.setCodeError(codError);


        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }


    public void requestClientExpressEnrollmentError(String documentNumber, Integer status, String msg){
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;

        try {
                    codigoError="130213";

                body = loadExpressEnrollmentRequestBodyError(documentNumber, channel, codigoError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testexp4" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .header(X_USER_RENIEC_HEADER, "DTBUSR")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/express-enrollment")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .log().all()
                    .extract()
                    .response()
                    .jsonPath();

            var messageError = response.getString("error.message");
            objIdentityEnrollmentSteps.setMessageError(messageError);
            var codError= response.getString("error.code");
            objIdentityEnrollmentSteps.setCodeError(codError);


        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }


    public void requestClientExpressEnrollmentError4(String documentNumber, Integer status, String msg){
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;

        try {
            codigoError="130214";

            body = loadExpressEnrollmentRequestBodyError(documentNumber, channel, codigoError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testexp5" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .header(X_USER_RENIEC_HEADER, "DTBUSR")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/express-enrollment")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .log().all()
                    .extract()
                    .response()
                    .jsonPath();

            var messageError = response.getString("error.message");
            objIdentityEnrollmentSteps.setMessageError(messageError);
            var codError= response.getString("error.code");
            objIdentityEnrollmentSteps.setCodeError(codError);


        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    public void requestClientExpressEnrollmentError5(String documentNumber, Integer status, String msg){
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;

        try {
            codigoError="130217";

            body = loadExpressEnrollmentRequestBodyError5(documentNumber, channel, codigoError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testexp6" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .header(X_USER_RENIEC_HEADER, "DTBUSR")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/express-enrollment")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .log().all()
                    .extract()
                    .response()
                    .jsonPath();

            var messageError = response.getString("error.message");
            objIdentityEnrollmentSteps.setMessageError(messageError);
            var codError= response.getString("error.code");
            objIdentityEnrollmentSteps.setCodeError(codError);


        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    public void requestClientExpressEnrollmentError6(String documentNumber, Integer status, String msg){
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;

        try {
            codigoError="130204";

            body = loadExpressEnrollmentRequestBodyError(documentNumber, channel, codigoError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testexp7" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .header(X_USER_RENIEC_HEADER, "BIEUSR")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/express-enrollment")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .log().all()
                    .extract()
                    .response()
                    .jsonPath();

            var messageError = response.getString("error.message");
            objIdentityEnrollmentSteps.setMessageError(messageError);
            var codError= response.getString("error.code");
            objIdentityEnrollmentSteps.setCodeError(codError);


        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }



    public void requestClientExpressEnrollmentError7(String documentNumber, Integer status, String msg){
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;

        try {
            codigoError="130211";

            body = loadExpressEnrollmentRequestBodyError5(documentNumber, channel, codigoError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testexp8" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .header(X_USER_RENIEC_HEADER, "BIEUSR")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/express-enrollment")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .log().all()
                    .extract()
                    .response()
                    .jsonPath();

            var messageError = response.getString("error.message");
            objIdentityEnrollmentSteps.setMessageError(messageError);
            var codError= response.getString("error.code");
            objIdentityEnrollmentSteps.setCodeError(codError);


        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }


    public void requestClientExpressEnrollmentError8(String documentNumber, Integer status, String msg){
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;

        try {
            codigoError="130219";

            body = loadExpressEnrollmentRequestBodyError(documentNumber, channel, codigoError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testexp9" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .header(X_USER_RENIEC_HEADER, "MMPUSR")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/express-enrollment")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .log().all()
                    .extract()
                    .response()
                    .jsonPath();

            var messageError = response.getString("error.message");
            objIdentityEnrollmentSteps.setMessageError(messageError);
            var codError= response.getString("error.code");
            objIdentityEnrollmentSteps.setCodeError(codError);


        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }


    public void requestClientExpressEnrollmentError9(String documentNumber, Integer status, String msg){
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;

        try {
            codigoError="130218";

            body = loadExpressEnrollmentRequestBodyError(documentNumber, channel, codigoError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testexp10" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .header(X_USER_RENIEC_HEADER, "MMPUSR")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/express-enrollment")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .log().all()
                    .extract()
                    .response()
                    .jsonPath();

            var messageError = response.getString("error.message");
            objIdentityEnrollmentSteps.setMessageError(messageError);
            var codError= response.getString("error.code");
            objIdentityEnrollmentSteps.setCodeError(codError);


        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }



}




