@IdentityExpressEnrollment
Feature: Client express enrollment
  @ExpressEnrollmentNewClient
  Scenario Outline: Enrolamiento express con un dni no enrolado previamente
    Given "<canal>" es un app registrado en OpenBanking
    And  el cliente con "<numeroDocumento>" se encuentra "<estado>"
    And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los enrolamientos
    When  el cliente con número de documento "<numeroDocumento>" envía una petición de enrolamiento express
    Then  se valida que el cliente con DNI  "<numeroDocumento>" se encuentre enrolado
    Examples:
      | numeroDocumento | estado       | canal |
      | 73006406        | NOT_ENROLLED | CANAL_EXTERNO |


  @ExpressEnrollmentNewClientErrorMask
  Scenario Outline: Error en enrolamiento express de un cliente nuevo cuando se toma foto con mascarilla
    Se espera conseguir el código de error 13.02.17 al no pasar la prueba de vida por rostro cubierto con mascarilla,
    el código de error no se contabiliza como un motivo de bloqueo.
    Given "<canal>" es un app registrado en OpenBanking
    And  el cliente con "<numeroDocumento>" se encuentra "<estado>"
    And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los  enrolamientos
    When  el cliente con número de documento "<numeroDocumento>" envía una petición de enrolamiento express con el selfie con "<msg>"
    Then  se valida que el cliente con DNI  "<numeroDocumento>" no pudo enrolarse
    And se valida que el cliente con DNI "<numeroDocumento>" tiene como respuesta el codigo de error  "<codigoError>" con mensaje de error "<mensajeError>"
    And el contador de bloqueo del cliente con DNI "<numeroDocumento>" no aumenta
    Examples:
      | numeroDocumento | estado       | canal | codigoError | mensajeError                                                        | msg                  |
      | 73006406        | NOT_ENROLLED | CANAL_EXTERNO | 13.02.17    | Bad request bfa service for passive Liveness validation FaceCropped | mascarilla           |


  @ExpressEnrollmentClienteErrorFraude
  Scenario Outline: Error en enrolamiento express de un cliente cuando se intenta tomar una foto y se detecta una condición de fraude
    Se espera conseguir el código de error 13.01.04 al no pasar la prueba de vida por fraude, el código de error se
    contabiliza como un motivo de bloqueo.
    Given "<canal>" es un app registrado en OpenBanking
    And  el cliente con "<numeroDocumento>" se encuentra "<estado>"
    And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los  enrolamientos
    When  el cliente con número de documento "<numeroDocumento>" envía una petición de enrolamiento express con el selfie con condición de "<msg>"
    Then el contador de bloqueo del cliente con DNI "<numeroDocumento>" aumenta en 1
    And se valida que el cliente con DNI "<numeroDocumento>" tiene como respuesta el codigo de error  "<codigoError>" con mensaje de error "<mensajeError>"
    Examples:
      | numeroDocumento | estado       | canal         | codigoError | mensajeError                          | msg    |
      | 73006406        | NOT_ENROLLED | NEXBI         | 13.01.04    | Customer does not pass the life test. | fraude |


  @ExpressEnrollmentClienteErrorMuchosRostros
  Scenario Outline: Error en enrolamiento express de un cliente porque no se pudo evaluar la prueba de vida debido a que existen demasiadas personas en la escena.
    Se espera conseguir el código de error 13.02.13 al no pasar la prueba de vida, el código de error no se
    contabiliza como un motivo de bloqueo.
    Given "<canal>" es un app registrado en OpenBanking
    And  el cliente con "<numeroDocumento>" se encuentra "<estado>"
    And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los  enrolamientos
    When el cliente con número de documento "<numeroDocumento>" envía una petición de enrolamiento express con error porque en el selfie existen "<msg>"
    Then se valida que el cliente con DNI  "<numeroDocumento>" no pudo enrolarse
    And se valida que el cliente con DNI "<numeroDocumento>" tiene como respuesta el codigo de error  "<codigoError>" con mensaje de error "<mensajeError>"
    And el contador de bloqueo del cliente con DNI "<numeroDocumento>" no aumenta
    Examples:
      | numeroDocumento | estado       | canal         | codigoError | mensajeError                                                         | msg           |
      | 73006406        | NOT_ENROLLED | TUNKI_APP     | 13.02.13    | Bad request bfa service for passive Liveness validation TooManyFaces | muchos rostros|

  @ExpressEnrollmentClienteErrorBajaCalidad
  Scenario Outline: Error en enrolamiento express de un cliente porque no se pudo evaluar la prueba de vida debido a la baja calidad del selfie.
  Se espera conseguir el código de error 13.02.14 al no pasar la prueba de vida, el código de error no se
  contabiliza como un motivo de bloqueo.
    Given "<canal>" es un app registrado en OpenBanking
    And  el cliente con "<numeroDocumento>" se encuentra "<estado>"
    And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los  enrolamientos
    When el cliente con número de documento "<numeroDocumento>" envía una petición de enrolamiento express con error debido a que el selfie tiene "<msg>"
    Then se valida que el cliente con DNI  "<numeroDocumento>" no pudo enrolarse
    And se valida que el cliente con DNI "<numeroDocumento>" tiene como respuesta el codigo de error  "<codigoError>" con mensaje de error "<mensajeError>"
    And el contador de bloqueo del cliente con DNI "<numeroDocumento>" no aumenta
    Examples:
      | numeroDocumento | estado       | canal         | codigoError | mensajeError                                                         | msg           |
      | 73006406        | NOT_ENROLLED | TUNKI_APP     | 13.02.14    | Bad request bfa service for passive Liveness validation BadQuality   | baja calidad  |

  @ExpressEnrollmentClienteErrorImagenRecortada
  Scenario Outline: Error en enrolamiento express de un cliente porque no se pudo evaluar la prueba de vida debido a que la imagen del selfie del cliente está recortada.
  Se espera conseguir el código de error 13.02.17 al no pasar la prueba de vida, el código de error no se
  contabiliza como un motivo de bloqueo.
    Given "<canal>" es un app registrado en OpenBanking
    And  el cliente con "<numeroDocumento>" se encuentra "<estado>"
    And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los  enrolamientos
    When el cliente con número de documento "<numeroDocumento>" envía una petición de enrolamiento express con error debido a que la imagen del selfie del cliente esta "<msg>"
    Then se valida que el cliente con DNI  "<numeroDocumento>" no pudo enrolarse
    And se valida que el cliente con DNI "<numeroDocumento>" tiene como respuesta el codigo de error  "<codigoError>" con mensaje de error "<mensajeError>"
    And el contador de bloqueo del cliente con DNI "<numeroDocumento>" no aumenta
    Examples:
      | numeroDocumento | estado       | canal         | codigoError | mensajeError                                                         | msg           |
      | 73006406        | NOT_ENROLLED | CANAL_EXTERNO     | 13.02.17    | Bad request bfa service for passive Liveness validation FaceCropped  | recortada     |


  @ExpressEnrollmentClienteErrorSimilitud
  Scenario Outline: Error en enrolamiento express de un cliente cuando se intenta tomar una foto y el selfie no  alcanzó el porcentaje minimo de similitud entre la foto de reniec con el selfie.
  Se espera conseguir el código de error 13.02.04 al no pasar la prueba de vida por fraude, el código de error se
  contabiliza como un motivo de bloqueo.
    Given "<canal>" es un app registrado en OpenBanking
    And  el cliente con "<numeroDocumento>" se encuentra "<estado>"
    And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los  enrolamientos
    When  el cliente con número de documento "<numeroDocumento>" envía una petición de enrolamiento express pero el selfie "<msg>"
    Then el contador de bloqueo del cliente con DNI "<numeroDocumento>" aumenta en 1
    And se valida que el cliente con DNI "<numeroDocumento>" tiene como respuesta el codigo de error  "<codigoError>" con mensaje de error "<mensajeError>"
    Examples:
      | numeroDocumento | estado       | canal         | codigoError | mensajeError                                            | msg    |
      | 73006406        | NOT_ENROLLED | NEXBI         | 13.02.04    | Negative authentication result., similarity: 0.0 | no alcanzó el porcentaje mínimo de similitud |


  @ExpressEnrollmentClientNotFoundInReniec
  Scenario Outline: Error en enrolamiento express porque el cliente no se encuentra en Reniec.
  Se espera conseguir el código de error 13.02.11 al no pasar la prueba de vida, el código de error no se
  contabiliza como un motivo de bloqueo.
    Given "<canal>" es un app registrado en OpenBanking
    And  el cliente con "<numeroDocumento>" se encuentra "<estado>"
    And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los  enrolamientos
    When el cliente con número de documento "<numeroDocumento>" envía una petición de enrolamiento express con error debido a que el cliente "<msg>"
    Then se valida que el cliente con DNI  "<numeroDocumento>" no pudo enrolarse
    And se valida que el cliente con DNI "<numeroDocumento>" tiene como respuesta el codigo de error  "<codigoError>" con mensaje de error "<mensajeError>"
    And el contador de bloqueo del cliente con DNI "<numeroDocumento>" no aumenta
    Examples:
      | numeroDocumento | estado       | canal         | codigoError | mensajeError        | msg                           |
      | 73006408        | NOT_ENROLLED | NEXBI         | 13.02.11    | Not found in Reniec | no se encuentra en reniec     |

  @ExpressEnrollmentClienteErrorRostroMuyCercaBorde
  Scenario Outline: Error en enrolamiento express porque el selfie del cliente está muy cerca al borde
  Se espera conseguir el código de error 13.02.19 al no pasar la prueba de vida, el código de error no se
  contabiliza como un motivo de bloqueo.
    Given "<canal>" es un app registrado en OpenBanking
    And  el cliente con "<numeroDocumento>" se encuentra "<estado>"
    And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los  enrolamientos
    When el cliente con número de documento "<numeroDocumento>" envía una petición de enrolamiento express con error debido a que el selfie del cliente tiene el "<msg>"
    Then se valida que el cliente con DNI  "<numeroDocumento>" no pudo enrolarse
    And se valida que el cliente con DNI "<numeroDocumento>" tiene como respuesta el codigo de error  "<codigoError>" con mensaje de error "<mensajeError>"
    And el contador de bloqueo del cliente con DNI "<numeroDocumento>" no aumenta
    Examples:
      | numeroDocumento | estado       | canal     | codigoError | mensajeError                                                                 | msg                           |
      | 73006406        | NOT_ENROLLED | TUNKI_APP | 13.02.19    | Bad request bfa service for passive Liveness validation FaceTooCloseToBorder | rostro muy cerca al borde    |

  @ExpressEnrollmentClienteErrorRostroCercaBorde
  Scenario Outline: Error en enrolamiento express porque el selfie del cliente está cerca al borde
  Se espera conseguir el código de error 13.02.18 al no pasar la prueba de vida, el código de error no se
  contabiliza como un motivo de bloqueo.
    Given "<canal>" es un app registrado en OpenBanking
    And  el cliente con "<numeroDocumento>" se encuentra "<estado>"
    And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los  enrolamientos
    When el cliente con número de documento "<numeroDocumento>" envía una petición de enrolamiento express con error debido a que el selfie del cliente tiene su "<msg>"
    Then se valida que el cliente con DNI  "<numeroDocumento>" no pudo enrolarse
    And se valida que el cliente con DNI "<numeroDocumento>" tiene como respuesta el codigo de error  "<codigoError>" con mensaje de error "<mensajeError>"
    And el contador de bloqueo del cliente con DNI "<numeroDocumento>" no aumenta
    Examples:
      | numeroDocumento | estado       | canal     | codigoError | mensajeError                                                         | msg                   |
      | 73006406        | NOT_ENROLLED | TUNKI_APP | 13.02.18    | Bad request bfa service for passive Liveness validation FaceTooClose | rostro cerca al borde |
