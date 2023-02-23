package pe.interbank.bfa.front.stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.apache.http.HttpStatus;
import pe.interbank.bfa.front.steps.identity.IdentityEnrollmentSteps;

public class IdentityEnrollmentDefinition {

    @Steps
    IdentityEnrollmentSteps identityEnrollmentSteps = new IdentityEnrollmentSteps();

    @Given("{string} es un app registrado en OpenBanking")
    public void app_registered_in_OpenBanking(String channel) {
        identityEnrollmentSteps.getAccessTokenOPBK();
        identityEnrollmentSteps.setChannel(channel);
    }

    @Given("el cliente con {string} se encuentra {string}")
    public void client_with_has_previous_enrolled(String documentNumber, String status) {
        identityEnrollmentSteps.requestClientEnrollmentStatus(documentNumber, status);
    }

    @And("el cliente con DNI {string} no se encuentra bloqueado en los enrolamientos")
    public void client_with_DNI_is_not_blocked(String documentNumber) {
        identityEnrollmentSteps.requestCustomerEnrollmentStatusLock(documentNumber);
    }

    @When("el cliente con numero de documento {string} envía una petición de enrolamiento Web")
    public void client_with_document_identity_number_is_requested_to_be_enrolled(String documentNumber) {
        identityEnrollmentSteps.requestExtractOcr(documentNumber);
        identityEnrollmentSteps.requestClientEnrollment(documentNumber);
    }

    @Then("se valida que el cliente con DNI  {string} se encuentre enrolado" )
    public void validate_enrollment_of_customer(String documentNumber) {
        identityEnrollmentSteps.validateEnrolledCustomer(documentNumber);
    }

    @When("el cliente con número de documento {string} envía una petición de enrolamiento tradicional")
    public void client_with_document_identity_number_is_requested_to_be_enrolled_traditional(String documentNumber) {
        identityEnrollmentSteps.requestClientEnrollmentTraditional(documentNumber, HttpStatus.SC_OK);
    }

    @When("el cliente con número de documento {string} envía una petición de enrolamiento tradicional con error en la comparacion entre la pantilla biometrica con la foto del cliente extraida del documento")
    public void client_with_document_identity_number_is_requested_to_be_enrolled_traditional_error(String documentNumber) {
        identityEnrollmentSteps.requestClientEnrollmentTraditionalError(documentNumber, HttpStatus.SC_BAD_REQUEST);
    }


    @And("se valida que cliente con DNI {string} tiene como respuesta el codigo de error  {string} con mensaje de error {string}")
    public void validate_message_and_error_code_from_response(String documentNumber,String errorCode, String errorMessage) {
        identityEnrollmentSteps.requestCustomerEnrollmentStatusLock(documentNumber);
        identityEnrollmentSteps.getValidateErrorCodeMessageEnrollment(documentNumber, errorCode, errorMessage, HttpStatus.SC_BAD_REQUEST);
    }
}
