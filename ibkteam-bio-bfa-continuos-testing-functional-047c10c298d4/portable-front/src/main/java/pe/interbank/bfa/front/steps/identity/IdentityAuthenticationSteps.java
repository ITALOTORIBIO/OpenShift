package pe.interbank.bfa.front.steps.identity;

import io.cucumber.core.exception.CucumberException;
import io.restassured.http.ContentType;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.interbank.bfa.front.configuration.OpbkConfiguration;
import pe.interbank.bfa.front.utils.Security;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static io.restassured.RestAssured.given;
import static pe.interbank.bfa.front.utils.Constants.*;
import static pe.interbank.bfa.front.utils.Constants.COMPANY_INTERBANK;

public class IdentityAuthenticationSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityAuthenticationSteps.class);

    public static final String TEMPLATES_PATH = "/src/main/resources/templates/";
    public static final String CHANNEL_JSON = "channel";
    public static final String APPLICATION_JSON = "application";
    public int currentFailedAttemptsBefore;
    public String messageError;
    public String codeError;

    IdentityEnrollmentSteps objIdentityEnrollmentSteps = new IdentityEnrollmentSteps();

    @Step("consultar si el cliente esta enrollado en el canal")
    public void findClientEnrollmentStatus() {
        String token = Security.createAccessTokenOPBK();
        LOGGER.info(token);
    }

    public void requestClientAuthenticationStatus(String documentNumber, String status) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        try {
            var clientStatus = getCustomerEnrollmentStatus(documentNumber, token, channel);
            if (!status.equals(clientStatus)) requestClientEnrollmentTraditional2(documentNumber);
        }catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    private void requestClientEnrollmentTraditional2(String documentNumber) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;

        try {
            body = requestClientEnrollmentTraditional(documentNumber, channel);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testAut1" + Instant.now().getEpochSecond())
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

    private JSONObject requestClientEnrollmentTraditional(String documentNumber, String channel) throws Exception {
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


    public void requestCustomerAuthenticationStatusLock(String documentNumber) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        try{

            var clientStatusLock =   getCustomerAuthenticationStatusLock(documentNumber, token, channel);
            if (clientStatusLock) fiendClientToUnlockAuthentication(documentNumber);
        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }


    }

    private void fiendClientToUnlockAuthentication(String documentNumber) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV).toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV).toString();
        given()
                .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                .header(AUTHORIZATION_HEADER, token)
                .header(CORRELATION_ID_HEADER,"toUnlock" + Instant.now().getEpochSecond())
                .queryParam(DOCUMENT_TYPE_PARAM,DOCUMENT_TYPE_DNI)
                .queryParam(DOCUMENT_NUMBER_PARAM, documentNumber)
                .queryParam(COMPANY_PARAM, COMPANY_INTERBANK)
                .queryParam(CHANNEL_PARAM,channel)
                .queryParam("operationType", "AUTHENTICATION")
                .log().all()
                .put(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/customer/unlock")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_NO_CONTENT)
        ;
    }

    private boolean getCustomerAuthenticationStatusLock(String documentNumber, String token, String channel) {
        var responseBody =  given()
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
        var blockStatus = responseBody.getBoolean("authenticateTrace.block");
        if (blockStatus) {
            Serenity.setSessionVariable(CURRENT_FAILED_ATTEMPTS).to("0");
        } else {
            var currentFailedAttempts = responseBody.getString("authenticateTrace.failedAttempts");
            Serenity.setSessionVariable(CURRENT_FAILED_ATTEMPTS).to(currentFailedAttempts);
        }
        return blockStatus;
    }

    public void getValidateMessageAuthentication(String documentNumber, String statusAuth, String descripcion) {
        if (statusAuth.equals(objIdentityEnrollmentSteps.getFacialStatus()) && descripcion.equals(objIdentityEnrollmentSteps.getFacialDescription())){
            System.out.println("*** Verificando mensaje y código de error ***");
            System.out.println("statusAuth: " + objIdentityEnrollmentSteps.getFacialStatus());
            System.out.println("descripcion: " + objIdentityEnrollmentSteps.getFacialDescription());
        }else {throw new CucumberException("No paso la validación");}
    }

    public void requestClientAuthentication(String documentNumber, Integer status, String msg) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;

        try {
            String statusAuth = "Positive";

            body = loadAuthemticateRequestBodyOk(documentNumber, channel, statusAuth);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testAut2" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .log().all()
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/user")
                    .then()
                    .assertThat()
                    .statusCode(status)
                    .log().all()
                    .extract()
                    .response()
                    .jsonPath();

            var facialStatus = response.getString("authentication.user.facial.status");
            objIdentityEnrollmentSteps.setFacialStatus(facialStatus);
            var facialDescription= response.getString("authentication.user.facial.description");
            objIdentityEnrollmentSteps.setFacialDescription(facialDescription);

        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    private JSONObject loadAuthemticateRequestBodyOk(String documentNumber,String channel, String statusAuth ) throws Exception{
        var path = System.getProperty("user.dir") + TEMPLATES_PATH + "identity-authenticate-" + documentNumber +"-" + statusAuth +".json";
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
        if (errorCode.equals(objIdentityEnrollmentSteps.getCodeError()) && errorMessage.equals(objIdentityEnrollmentSteps.getMessageError())){

            System.out.println("*** Verificando mensaje y código de error ***");
            System.out.println("messageError: " + objIdentityEnrollmentSteps.getMessageError());
            System.out.println("codeError: " + objIdentityEnrollmentSteps.getCodeError());

        }else {throw new CucumberException("No paso la validación");}
    }

    public void requestValidateCountBlockIncrement(String documentNumber) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        try {
            var countFailedAttemptsLater = getCountFailedAttemptsLater(documentNumber, token, channel);
            if(countFailedAttemptsLater == objIdentityEnrollmentSteps.getCountFailedBefore()+1);
            System.out.println("Se verifica que no aumenta la cantidad de intentos fallidos");
            System.out.println("countFailedAttempts-Antes: "+objIdentityEnrollmentSteps.getCountFailedBefore());
            System.out.println("countFailedAttempts-Después: "+countFailedAttemptsLater);
        } catch (Exception ex) {
            throw new CucumberException(ex.getMessage());
        }
    }

    private int getCountFailedAttemptsLater(String documentNumber, String token, String channel) {
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
        var currentFailedAttemptsLater = responseBody.getInt("authenticateTrace.failedAttempts");
        return currentFailedAttemptsLater;
    }

    public void request_Validate_Authentication_Count_blocked_not_increment(String documentNumber) {
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

    public void requestClientAuthenticationError1(String documentNumber, Integer status, String msg) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;
        try {
            codeError="130104";
            body = loadAuthenticateRequestBodyError(documentNumber, channel, codeError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testAut3" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .log().all()
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/user")
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

    private JSONObject loadAuthenticateRequestBodyError(String documentNumber, String channel, String codigoError) throws Exception  {
        var path = System.getProperty("user.dir") + TEMPLATES_PATH + "identity-authenticate-" + documentNumber +"-" + codigoError +"-error"+".json";
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

    public void requestClientAuthenticationError2(String documentNumber, Integer status, String msg) {
        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;
        try {
            codeError="130217";
            body = loadAuthenticateRequestBodyError2(documentNumber, channel, codeError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testAut4" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .log().all()
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/user")
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

    private JSONObject loadAuthenticateRequestBodyError2(String documentNumber, String channel, String codigoError)throws Exception  {
        var path = System.getProperty("user.dir") + TEMPLATES_PATH + "identity-authenticate-" + documentNumber +"-" + codigoError +"-error"+".json";
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

    public void requestClientAuthenticationError3(String documentNumber, Integer status, String msg) {

        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;
        try {
            codeError="130213";
            body = loadAuthenticateRequestBodyError2(documentNumber, channel, codeError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testAut5" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .log().all()
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/user")
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


    public void requestClientAuthenticationError4(String documentNumber, Integer status, String msg) {

        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;
        try {
            codeError="130217";
            body = loadAuthenticateRequestBodyError4(documentNumber, channel, codeError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testAut6" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .log().all()
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/user")
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

    private JSONObject loadAuthenticateRequestBodyError4(String documentNumber, String channel, String codeError) throws Exception  {
        var path = System.getProperty("user.dir") + TEMPLATES_PATH + "identity-authenticate-" + documentNumber +"-" + codeError +"-error-Msk"+".json";
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

    public void requestClientAuthenticationError5(String documentNumber, Integer status, String msg) {

        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;
        try {
            codeError="130214";
            body = loadAuthenticateRequestBodyError2(documentNumber, channel, codeError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testAut7" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .log().all()
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/user")
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

    public void requestClientAuthenticationError6(String documentNumber, Integer status, String msg) {

        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;
        try {
            codeError="130216";
            body = loadAuthenticateRequestBodyError2(documentNumber, channel, codeError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testAut8" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .log().all()
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/user")
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

    public void requestClientAuthenticationError7(String documentNumber, Integer status, String msg) {

        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;
        try {
            codeError="130218";
            body = loadAuthenticateRequestBodyError2(documentNumber, channel, codeError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testAut9" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .log().all()
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/user")
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

    public void requestClientAuthenticationError8(String documentNumber, Integer status, String msg) {

        var token = Serenity.sessionVariableCalled(OPBK_TOKEN_ENV)
                .toString();
        var channel = Serenity.sessionVariableCalled(CHANNEL_ENV)
                .toString();
        JSONObject body;
        try {
            codeError="130219";
            body = loadAuthenticateRequestBodyError2(documentNumber, channel, codeError);
            var response = given()
                    .header(SUBSCRIPTION_KEY_HEADER, OpbkConfiguration.getIdentitySubscriptionKey())
                    .header(AUTHORIZATION_HEADER, token)
                    .header(CORRELATION_ID_HEADER, "testAut10" + Instant.now().getEpochSecond())
                    .header(X_API_VERSION_HEADER, "1.0")
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .log().all()
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/identity/v1/identity/user")
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
