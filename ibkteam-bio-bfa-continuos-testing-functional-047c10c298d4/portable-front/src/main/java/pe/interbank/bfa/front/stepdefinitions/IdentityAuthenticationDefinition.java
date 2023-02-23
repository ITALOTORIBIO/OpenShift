package pe.interbank.bfa.front.stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.apache.http.HttpStatus;
import pe.interbank.bfa.front.steps.identity.IdentityAuthenticationSteps;

public class IdentityAuthenticationDefinition {

    @Steps
    IdentityAuthenticationSteps authenticationSteps = new IdentityAuthenticationSteps();


    @And("el cliente con {string} se encuentra {string} y se puede autenticar")
    public void client_with_has_previous_enrolled(String documentNumber, String status) {
        authenticationSteps.requestClientAuthenticationStatus(documentNumber, status);
    }

    @And("el cliente con {string} no se encuentra bloqueado en la autenticacion")
    public void client_with_DNI_is_not_blocked_authenticate(String documentNumber) {
        authenticationSteps.requestCustomerAuthenticationStatusLock(documentNumber);
    }

    @Then("se valida que el cliente con DNi {string} tiene como respuesta de autenticacion exitosa el status {string} con descripcion {string}")
    public void seValidaQueElClienteConDNiTieneComoRespuestaDeAutenticacionExitosa(String documentNumber, String statusAuth, String descripcion) {
        authenticationSteps.getValidateMessageAuthentication(documentNumber, statusAuth,descripcion);


    }

    @When("el cliente con numero de documento {string} envia una peticion de autenticacion {string}")
    public void elClienteConNumeroDeDocumentoEnviaUnaPeticionDeAutenticacion(String documentNumber , String msg) {
        authenticationSteps.requestClientAuthentication(documentNumber, HttpStatus.SC_OK, msg);
    }

    @Then("se valida que el cliente con DNI {string} tiene como respuesta de autenticacion el codigo de error  {string} con mensaje de error {string}")
    public void validateMessageAndErrorCodeFromResponseAuthentication(String documentNumber, String errorCode, String errorMessage) {
        authenticationSteps.getValidateErrorCodeMessage(documentNumber,errorCode,errorMessage, HttpStatus.SC_BAD_REQUEST);
    }

    @And("el contador de bloqueo de autenticacion del cliente con DNI {string} aumenta en {int}")
    public void elContadorDeBloqueoDeAutenticacionDelClienteConDNIAumentaEn(String documentNumber, int arg1) {
        authenticationSteps.requestValidateCountBlockIncrement(documentNumber);
    }

    @And("el contador de bloqueo de autenticacion del cliente con DNI {string} no aumenta")
    public void elContadorDeBloqueoDeAutenticacionDelClienteConDNINoAumenta(String documentNumber) {
        authenticationSteps.request_Validate_Authentication_Count_blocked_not_increment(documentNumber);
    }

    @When("el cliente con numero de documento {string} envia una peticion de autenticacion con error por {string}")
    public void elClienteConNumeroDeDocumentoEnviaUnaPeticionDeAutenticacionConErroPor(String documentNumber, String msg) {
        authenticationSteps.requestClientAuthenticationError1(documentNumber, HttpStatus.SC_BAD_REQUEST, msg);
    }

    @When("el cliente con numero de documento {string} envia una peticion de autenticacion con error debido a que en la imagen del selfie el cliente esta con {string}")
    public void elClienteConNumeroDeDocumentoEnviaUnaPeticionDeAutenticacionConErrorDebidoAQueEnLaImagenDelSelfieElClienteEstaCon(String documentNumber, String msg) {
        authenticationSteps.requestClientAuthenticationError2(documentNumber, HttpStatus.SC_BAD_REQUEST, msg);
    }

    @When("el cliente con numero de documento {string} envia una peticion de autenticacion con error porque en el selfie existen {string}")
    public void elClienteConNumeroDeDocumentoEnviaUnaPeticionDeAutenticacionConErrorPorqueEnElSelfieExisten(String documentNumber, String msg) {
        authenticationSteps.requestClientAuthenticationError3(documentNumber, HttpStatus.SC_NOT_FOUND, msg);
    }

    @When("el cliente con numero de documento {string} envia una peticion de autenticacion con error con el selfie con {string}")
    public void elClienteConNumeroDeDocumentoEnviaUnaPeticionDeAutenticacionConErrorConElSelfieCon(String documentNumber, String msg) {
        authenticationSteps.requestClientAuthenticationError4(documentNumber, HttpStatus.SC_BAD_REQUEST, msg);
    }

    @When("el cliente con numero de documento {string} envia una peticion de autenticacion con error debido a que el selfie tiene {string}")
    public void elClienteConNumeroDeDocumentoEnviaUnaPeticionDeAutenticacionConErrorDebidoAQueElSelfieTiene(String documentNumber, String msg) {
        authenticationSteps.requestClientAuthenticationError5(documentNumber, HttpStatus.SC_BAD_REQUEST, msg);
    }

    @When("el cliente con numero de documento {string} envia una peticion de autenticacion con error debido a que el selfie no tiene {string}")
    public void elClienteConNumeroDeDocumentoEnviaUnaPeticionDeAutenticacionConErrorDebidoAQueElSelfieNoTiene(String documentNumber, String msg) {
        authenticationSteps.requestClientAuthenticationError6(documentNumber, HttpStatus.SC_BAD_REQUEST, msg);
    }

    @When("el cliente con numero de documento {string} envia una peticion de autenticacion con error debido a que en la foto del selfie se detecto un {string}")
    public void elClienteConNumeroDeDocumentoEnviaUnaPeticionDeAutenticacionConErrorDebidoAQueEnLaFotoDelSelfieSeDetectoUn(String documentNumber, String msg) {
        authenticationSteps.requestClientAuthenticationError7(documentNumber, HttpStatus.SC_BAD_REQUEST, msg);
    }

    @When("el cliente con numero de documento {string} envia una peticion de autenticacion con error debido a que se detecto un rostro {string}")
    public void elClienteConNumeroDeDocumentoEnviaUnaPeticionDeAutenticacionConErrorDebidoAQueSeDetectoUnRostro(String documentNumber, String msg) {
        authenticationSteps.requestClientAuthenticationError8(documentNumber, HttpStatus.SC_BAD_REQUEST, msg);
    }
}
