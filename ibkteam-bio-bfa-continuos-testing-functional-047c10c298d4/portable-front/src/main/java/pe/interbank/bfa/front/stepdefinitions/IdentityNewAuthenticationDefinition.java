package pe.interbank.bfa.front.stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.apache.http.HttpStatus;
import pe.interbank.bfa.front.steps.identityAuth.IdentityNewAuthenticationSteps;

public class IdentityNewAuthenticationDefinition {

    @Steps
    IdentityNewAuthenticationSteps newAuthenticationSteps = new IdentityNewAuthenticationSteps();

    @And("el cliente con DNI {string} no se encuentra bloqueado en los enrolamiento identity-auth")
    public void client_with_DNI_is_not_blocked(String documentNumber) {
        newAuthenticationSteps.request2CustomerEnrollmentStatusLock(documentNumber);
    }


    @And("el cliente con {string} no se encuentra bloqueado en la autenticacion identity-auth")
    public void elClienteConNoSeEncuentraBloqueadoEnLaAutenticacionIdentityAuth(String documentNumber) {
        newAuthenticationSteps.requestCustomerNewAuthenticationStatusLock(documentNumber);
    }

    @When("el cliente con numero de documento {string} envia una peticion de autenticacion {string} a identity-auth")
    public void elClienteConNumeroDeDocumentoEnviaUnaPeticionDeAutenticacionAIdentityAuth(String documentNumber, String msg) {
        newAuthenticationSteps.requestClientNewAuthentication(documentNumber, HttpStatus.SC_OK, msg);
    }

    @Then("se valida que el cliente con DNI {string} tiene como respuesta de autenticacion exitosa el status {string} con descripcion {string}")
    public void seValidaQueElClienteConDNITieneComoRespuestaDeAutenticacionExitosaElStatusConDescripcion(String documentNumber, String statusAuth, String descripcion) {
        newAuthenticationSteps.getValidateMessageNewAuthentication(documentNumber, statusAuth, descripcion);
    }

    @When("el cliente con numero de documento {string} envia una peticion de autenticacion con error por {string} a identity-auth")
    public void elClienteConNumeroDeDocumentoEnviaUnaPeticionDeAutenticacionConErrorPorAIdentityAuth(String documentNumber, String msg) {
        newAuthenticationSteps.requestClientNewAuthenticationError1(documentNumber, HttpStatus.SC_BAD_REQUEST, msg);
    }

    @Then("se valida que el cliente con DNI {string} tiene como respuesta del nuevo servicio de autenticacion el codigo de error  {string} con mensaje de error {string}")
    public void seValidaQueElClienteConDNITieneComoRespuestaDelNuevoServicioDeAutenticacionElCodigoDeErrorConMensajeDeError(String documentNumber, String errorCode, String errorMessage) {
        newAuthenticationSteps.getValidateErrorCodeMessage(documentNumber, errorCode, errorMessage, HttpStatus.SC_BAD_REQUEST);
    }

    @When("el cliente con numero de documento {string} envia una peticion de autenticacion con error debido a que en la imagen del selfie el cliente esta con {string} a identity-auth")
    public void elClienteConNumeroDeDocumentoEnviaUnaPeticionDeAutenticacionConErrorDebidoAQueEnLaImagenDelSelfieElClienteEstaConAIdentityAuth(String documentNumber, String msg) {
        newAuthenticationSteps.requestClientNewAuthenticationError2(documentNumber, HttpStatus.SC_BAD_REQUEST, msg);
    }

    @When("el cliente con numero de documento {string} envia una peticion de autenticacion con error porque en el selfie existen {string} a identity-auth")
    public void elClienteConNumeroDeDocumentoEnviaUnaPeticionDeAutenticacionConErrorPorqueEnElSelfieExistenAIdentityAuth(String documentNumber, String msg) {
        newAuthenticationSteps.requestClientNewAuthenticationError3(documentNumber, HttpStatus.SC_NOT_FOUND, msg);
    }

    @When("el cliente con numero de documento {string} envia una peticion de autenticacion con error con el selfie con {string} a identity-auth")
    public void elClienteConNumeroDeDocumentoEnviaUnaPeticionDeAutenticacionConErrorConElSelfieConAIdentityAuth(String documentNumber, String msg) {
        newAuthenticationSteps.requestClientNewAuthenticationError4(documentNumber, HttpStatus.SC_BAD_REQUEST, msg);
    }

    @When("el cliente con numero de documento {string} envia una peticion de autenticacion con error debido a que el selfie tiene {string} a identity-auth")
    public void elClienteConNumeroDeDocumentoEnviaUnaPeticionDeAutenticacionConErrorDebidoAQueElSelfieTieneAIdentityAuth(String documentNumber, String msg) {
        newAuthenticationSteps.requestClientNewAuthenticationError5(documentNumber, HttpStatus.SC_BAD_REQUEST, msg);
    }

    @When("el cliente con numero de documento {string} envia una peticion de autenticacion con error debido a que el selfie no tiene {string} a identity-auth")
    public void elClienteConNumeroDeDocumentoEnviaUnaPeticionDeAutenticacionConErrorDebidoAQueElSelfieNoTieneAIdentityAuth(String documentNumber, String msg) {
        newAuthenticationSteps.requestClientNewAuthenticationError6(documentNumber, HttpStatus.SC_BAD_REQUEST, msg);
    }

    @When("el cliente con numero de documento {string} envia una peticion de autenticacion con error debido a que en la foto del selfie se detecto un {string} a identity-auth")
    public void elClienteConNumeroDeDocumentoEnviaUnaPeticionDeAutenticacionConErrorDebidoAQueEnLaFotoDelSelfieSeDetectoUnAIdentityAuth(String documentNumber, String msg) {
        newAuthenticationSteps.requestClientNewAuthenticationError7(documentNumber, HttpStatus.SC_BAD_REQUEST, msg);
    }

    @When("el cliente con numero de documento {string} envia una peticion de autenticacion con error debido a que se detecto un rostro {string} a identity-auth")
    public void elClienteConNumeroDeDocumentoEnviaUnaPeticionDeAutenticacionConErrorDebidoAQueSeDetectoUnRostroAIdentityAuth(String documentNumber, String msg) {
        newAuthenticationSteps.requestClientNewAuthenticationError8(documentNumber, HttpStatus.SC_BAD_REQUEST, msg);
    }
}
