@IdentityEnrollment
Feature: Enrolamiento de cliente con DNI

  @WebEnrollmentNewClient
  Scenario Outline: Enrolamiento web con un DNI no enrolado previamente
  Given "<canal>" es un app registrado en OpenBanking
  And  el cliente con "<numeroDocumento>" se encuentra "<estado>"
  And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los enrolamientos
  When el cliente con numero de documento "<numeroDocumento>" envía una petición de enrolamiento Web
  Then  se valida que el cliente con DNI  "<numeroDocumento>" se encuentre enrolado
Examples:
  | numeroDocumento | estado       | canal         |
  | 43729336        | NOT_ENROLLED | CANAL_EXTERNO |

  @EnrollmentNewClient
  Scenario Outline: Enrolamiento tradicional con un dni no enrolado previamente
  Given "<canal>" es un app registrado en OpenBanking
  And  el cliente con "<numeroDocumento>" se encuentra "<estado>"
  And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los enrolamientos
  When el cliente con número de documento "<numeroDocumento>" envía una petición de enrolamiento tradicional
  Then se valida que el cliente con DNI  "<numeroDocumento>" se encuentre enrolado
  Examples:
    | numeroDocumento | estado       | canal         |
    | 43729336        | NOT_ENROLLED | CANAL_EXTERNO |

  @EnrollmentErrorNegativeAuthenticationResult @TF01002
  Scenario Outline: Error en la comparacion entre la pantilla biometrica con la foto del cliente extraida del documento
    Se espera conseguir el código de error 13.02.04 al no pasar la similitud, el código de error se
    contabiliza como un motivo de bloqueo.

    Given "<canal>" es un app registrado en OpenBanking
    And  el cliente con "<numeroDocumento>" se encuentra "<estado>"
    And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los enrolamientos
    When el cliente con número de documento "<numeroDocumento>" envía una petición de enrolamiento tradicional con error en la comparacion entre la pantilla biometrica con la foto del cliente extraida del documento
    And se valida que cliente con DNI "<numeroDocumento>" tiene como respuesta el codigo de error  "<codigoError>" con mensaje de error "<mensajeError>"
    Then el contador de bloqueo del cliente con DNI "<numeroDocumento>" aumenta en 1

    Examples:
      | numeroDocumento | canal         | estado       | codigoError | mensajeError                                     |
      | 43729336        | CANAL_EXTERNO | NOT_ENROLLED | 13.02.04    | Negative authentication result., similarity: 0.0 |
