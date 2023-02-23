package pe.interbank.bfa.front.stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.Steps;
import org.apache.http.HttpStatus;
import pe.interbank.bfa.front.steps.identity.IdentityExpressEnrollmentSteps;

public class IdentityExpressEnrollmentDefinition {

    @Steps
    IdentityExpressEnrollmentSteps identityExpressEnrollmentSteps = new IdentityExpressEnrollmentSteps();

    @When("el cliente con número de documento {string} envía una petición de enrolamiento express")
    public void client_with_document_identity_number_is_requested_to_be_express_enrolled(String documentNumber) {
        identityExpressEnrollmentSteps.requestClientExpressEnrollment(documentNumber);
    }


    @And("el cliente con DNI {string} no se encuentra bloqueado en los  enrolamientos")
    public void client_with_DNI_is_not_blocked(String documentNumber) {
        identityExpressEnrollmentSteps.requestValidateUnlockCustomer(documentNumber);
    }

    @When("el cliente con número de documento {string} envía una petición de enrolamiento express con el selfie con {string}")
    public void client_with_document_identity_number_is_requested_to_be_express_enrolled_error_mask(String documentNumber, String msg) {
        identityExpressEnrollmentSteps.requestClientExpressEnrollmentError1(documentNumber, HttpStatus.SC_BAD_REQUEST,msg);
    }


    @Then("se valida que el cliente con DNI  {string} no pudo enrolarse")
    public void validate_enrollment_of_customer(String documentNumber) {
        identityExpressEnrollmentSteps.validateExpressEnrolledCustomer(documentNumber);
    }


    @And("se valida que el cliente con DNI {string} tiene como respuesta el codigo de error  {string} con mensaje de error {string}")
    public void validateMessageAndErrorCodeFromResponse(String documentNumber,String errorCode, String errorMessage) {
       identityExpressEnrollmentSteps.getValidateErrorCodeMessage(documentNumber,errorCode,errorMessage, HttpStatus.SC_BAD_REQUEST);
    }


    @And("el contador de bloqueo del cliente con DNI {string} no aumenta")
    public void count_blocked_not_increment(String documentNumber) {
        identityExpressEnrollmentSteps.request_Validate_Count_blocked_not_increment(documentNumber); //requestValidateCountFailedAttemptsLater
    }

    @When("el cliente con número de documento {string} envía una petición de enrolamiento express con el selfie con condición de {string}")
    public void client_with_document_identity_number_is_requested_to_be_express_enrolled_error_fraude(String documentNumber, String msg) {
        //identityExpressEnrollmentSteps.requestClientExpressEnrollmentErrorFraude(documentNumber, HttpStatus.SC_BAD_REQUEST);
        identityExpressEnrollmentSteps.requestClientExpressEnrollmentError2(documentNumber, HttpStatus.SC_BAD_REQUEST,msg);
    }

    @Then("el contador de bloqueo del cliente con DNI {string} aumenta en 1")
    public void elContadorDeBloqueoDelClienteConDNIAumentaEn(String documentNumber) {
        identityExpressEnrollmentSteps.requestValidateCountBlockIncrement(documentNumber);
    }

    @When("el cliente con número de documento {string} envía una petición de enrolamiento express con error porque en el selfie existen {string}")
    public void elClienteConNúmeroDeDocumentoEnvíaUnaPeticiónDeEnrolamientoExpressConErrorPorqueEnElSelfieExistenMuchasPersonas(String documentNumber, String msg) {
        identityExpressEnrollmentSteps.requestClientExpressEnrollmentError(documentNumber, HttpStatus.SC_NOT_FOUND,msg);
    }

    @When("el cliente con número de documento {string} envía una petición de enrolamiento express con error debido a que el selfie tiene {string}")
    public void elClienteConNúmeroDeDocumentoEnvíaUnaPeticiónDeEnrolamientoExpressConErrorBajaCalidad(String documentNumber, String msg) {
        identityExpressEnrollmentSteps.requestClientExpressEnrollmentError4(documentNumber, HttpStatus.SC_BAD_REQUEST,msg);
    }

    @When("el cliente con número de documento {string} envía una petición de enrolamiento express con error debido a que la imagen del selfie del cliente esta {string}")
    public void el_cliente_con_número_de_documento_envía_una_petición_de_enrolamiento_express_con_error_debido_a_que_la_imagen_del_selfie_del_cliente_esta(String documentNumber, String msg) {
        identityExpressEnrollmentSteps.requestClientExpressEnrollmentError5(documentNumber, HttpStatus.SC_BAD_REQUEST,msg);
    }

    @When("el cliente con número de documento {string} envía una petición de enrolamiento express pero el selfie {string}")
    public void elClienteConNúmeroDeDocumentoEnvíaUnaPeticiónDeEnrolamientoExpressSimilitud(String documentNumber, String msg) {
        identityExpressEnrollmentSteps.requestClientExpressEnrollmentError6(documentNumber, HttpStatus.SC_BAD_REQUEST,msg);
    }

    @When("el cliente con número de documento {string} envía una petición de enrolamiento express con error debido a que el cliente {string}")
    public void ClienteEnvíaPeticiónDeEnrollExpressErrorNotFoundReniec(String documentNumber, String msg) {
        identityExpressEnrollmentSteps.requestClientExpressEnrollmentError7(documentNumber, HttpStatus.SC_NOT_FOUND, msg
        );
    }

    @When("el cliente con número de documento {string} envía una petición de enrolamiento express con error debido a que el selfie del cliente tiene el {string}")
    public void ClienteEnvíaPeticiónDeEnrollExpressErrorRostroMuyCercaBorde(String documentNumber, String msg) {
        identityExpressEnrollmentSteps.requestClientExpressEnrollmentError8(documentNumber, HttpStatus.SC_BAD_REQUEST, msg);
    }

    @When("el cliente con número de documento {string} envía una petición de enrolamiento express con error debido a que el selfie del cliente tiene su {string}")
    public void ClienteEnvíaPeticiónDeEnrollExpressErrorRostroCercaBorde(String documentNumber, String msg) {
        identityExpressEnrollmentSteps.requestClientExpressEnrollmentError9(documentNumber, HttpStatus.SC_BAD_REQUEST, msg);}
}
