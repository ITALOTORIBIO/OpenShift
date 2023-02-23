@IdentityNewAuthentication
Feature: Autenticación de un cliente con DNI desde el api modularizado identity-auth

  @NewAuthenticationClientEnrolled
  Scenario Outline: Autenticación exitosa de un cliente DNI desde las apis de identity-auth.
    Given "<canal>" es un app registrado en OpenBanking
    And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los enrolamiento identity-auth
    And el cliente con "<numeroDocumento>" se encuentra "<estado>" y se puede autenticar
    And el cliente con "<numeroDocumento>" no se encuentra bloqueado en la autenticacion identity-auth
    When el cliente con numero de documento "<numeroDocumento>" envia una peticion de autenticacion "<statusAuth>" a identity-auth
    Then se valida que el cliente con DNI "<numeroDocumento>" tiene como respuesta de autenticacion exitosa el status "<statusAuth>" con descripcion "<descripcion>"

    Examples:
      | numeroDocumento | canal         | estado   | statusAuth | descripcion                     |
      | 43729336        | CANAL_EXTERNO | ENROLLED | Positive   | Positive authentication result. |
      | 73006406        | CANAL_EXTERNO | ENROLLED | Positive   | Positive authentication result. |

  @IdentityNewAuthenticationClienteErrorFraude
  Scenario Outline:Error en la autenticacion de un cliente cuando se intenta usar una foto y se detecta una condición de fraude.
  Se espera conseguir el código de error 13.01.04 al no pasar la prueba de vida por fraude, el código de error se
  contabiliza como un motivo de bloqueo.
    Given "<canal>" es un app registrado en OpenBanking
    And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los enrolamiento identity-auth
    And el cliente con "<numeroDocumento>" se encuentra "<estado>" y se puede autenticar
    And el cliente con "<numeroDocumento>" no se encuentra bloqueado en la autenticacion identity-auth
    When el cliente con numero de documento "<numeroDocumento>" envia una peticion de autenticacion con error por "<estado1>" a identity-auth
    Then se valida que el cliente con DNI "<numeroDocumento>" tiene como respuesta del nuevo servicio de autenticacion el codigo de error  "<codigoError>" con mensaje de error "<mensajeError>"
    And el contador de bloqueo de autenticacion del cliente con DNI "<numeroDocumento>" aumenta en 1

    Examples:
      | numeroDocumento | estado   | canal         | codigoError | mensajeError                          | estado1 |
      | 73006406        | ENROLLED | CANAL_EXTERNO | 13.01.04    | Customer does not pass the life test. | fraude  |

  @IdentityNewAuthenticationClienteErrorImagenRecortada
  Scenario Outline: Error en la autenticacion de un cliente porque no se pudo evaluar la prueba de vida debido a que la imagen del selfie del cliente está recortada.
  Se espera conseguir el código de error 13.02.17 al no pasar la prueba de vida, el código de error no se
  contabiliza como un motivo de bloqueo.

    Given "<canal>" es un app registrado en OpenBanking
    And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los enrolamiento identity-auth
    And el cliente con "<numeroDocumento>" se encuentra "<estado>" y se puede autenticar
    And el cliente con "<numeroDocumento>" no se encuentra bloqueado en la autenticacion identity-auth
    When el cliente con numero de documento "<numeroDocumento>" envia una peticion de autenticacion con error debido a que en la imagen del selfie el cliente esta con "<estado1>" a identity-auth
    Then se valida que el cliente con DNI "<numeroDocumento>" tiene como respuesta del nuevo servicio de autenticacion el codigo de error  "<codigoError>" con mensaje de error "<mensajeError>"
    And el contador de bloqueo de autenticacion del cliente con DNI "<numeroDocumento>" no aumenta

    Examples:
      | numeroDocumento | estado   | canal         | codigoError | mensajeError                                                        | estado1   |
      | 73006406        | ENROLLED | CANAL_EXTERNO | 13.02.17    | Bad request bfa service for passive Liveness validation FaceCropped | recortada |

  @IdentityNewAuthenticationClienteErrorMuchosRostros
  Scenario Outline: Error en la autenticacion de un cliente porque no se pudo evaluar la prueba de vida debido a que existen demasiadas personas en la escena.
  Se espera conseguir el código de error 13.02.13 al no pasar la prueba de vida, el código de error no se
  contabiliza como un motivo de bloqueo.

    Given "<canal>" es un app registrado en OpenBanking
    And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los enrolamiento identity-auth
    And el cliente con "<numeroDocumento>" se encuentra "<estado>" y se puede autenticar
    And el cliente con "<numeroDocumento>" no se encuentra bloqueado en la autenticacion identity-auth
    When el cliente con numero de documento "<numeroDocumento>" envia una peticion de autenticacion con error porque en el selfie existen "<estado1>" a identity-auth
    Then se valida que el cliente con DNI "<numeroDocumento>" tiene como respuesta del nuevo servicio de autenticacion el codigo de error  "<codigoError>" con mensaje de error "<mensajeError>"
    And el contador de bloqueo de autenticacion del cliente con DNI "<numeroDocumento>" no aumenta

    Examples:
      | numeroDocumento | estado   | canal         | codigoError | mensajeError                                                         | estado1        |
      | 73006406        | ENROLLED | CANAL_EXTERNO | 13.02.13    | Bad request bfa service for passive Liveness validation TooManyFaces | muchos rostros |

  @IdentityNewAuthenticationClienteErrorMask
  Scenario Outline: Error en la autenticacion de un cliente nuevo cuando se toma foto con mascarilla
  Se espera conseguir el código de error 13.02.17 al no pasar la prueba de vida por rostro cubierto con mascarilla,
  el código de error no se contabiliza como un motivo de bloqueo.

    Given "<canal>" es un app registrado en OpenBanking
    And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los enrolamiento identity-auth
    And el cliente con "<numeroDocumento>" se encuentra "<estado>" y se puede autenticar
    And el cliente con "<numeroDocumento>" no se encuentra bloqueado en la autenticacion identity-auth
    When el cliente con numero de documento "<numeroDocumento>" envia una peticion de autenticacion con error con el selfie con "<estado1>" a identity-auth
    Then se valida que el cliente con DNI "<numeroDocumento>" tiene como respuesta del nuevo servicio de autenticacion el codigo de error  "<codigoError>" con mensaje de error "<mensajeError>"
    And el contador de bloqueo de autenticacion del cliente con DNI "<numeroDocumento>" no aumenta

    Examples:
      | numeroDocumento | estado   | canal         | codigoError | mensajeError                                                        | estado1    |
      | 73006406        | ENROLLED | CANAL_EXTERNO | 13.02.17    | Bad request bfa service for passive Liveness validation FaceCropped | Mascarilla |

  @IdentityNewAuthenticationClienteErrorBajaCalidad
  Scenario Outline: Error en la autenticacion de un cliente porque no se pudo evaluar la prueba de vida debido a la baja calidad de la foto del selfie.
  Se espera conseguir el código de error 13.02.14 al no pasar la prueba de vida, el código de error no se
  contabiliza como un motivo de bloqueo.

    Given "<canal>" es un app registrado en OpenBanking
    And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los enrolamiento identity-auth
    And el cliente con "<numeroDocumento>" se encuentra "<estado>" y se puede autenticar
    And el cliente con "<numeroDocumento>" no se encuentra bloqueado en la autenticacion identity-auth
    When el cliente con numero de documento "<numeroDocumento>" envia una peticion de autenticacion con error debido a que el selfie tiene "<estado1>" a identity-auth
    Then se valida que el cliente con DNI "<numeroDocumento>" tiene como respuesta del nuevo servicio de autenticacion el codigo de error  "<codigoError>" con mensaje de error "<mensajeError>"
    And el contador de bloqueo de autenticacion del cliente con DNI "<numeroDocumento>" no aumenta

    Examples:
      | numeroDocumento | estado   | canal         | codigoError | mensajeError                                                       | estado1      |
      | 73006406        | ENROLLED | CANAL_EXTERNO | 13.02.14    | Bad request bfa service for passive Liveness validation BadQuality | baja calidad |

  @IdentityNewAuthenticationClienteErrorNoDetectaRostro
  Scenario Outline: Error en la autenticacion de un cliente porque no se pudo detectar un rostro en la foto del selfie.
  Se espera conseguir el código de error 13.02.16 al no pasar la prueba de vida, el código de error no se
  contabiliza como un motivo de bloqueo.

    Given "<canal>" es un app registrado en OpenBanking
    And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los enrolamiento identity-auth
    And el cliente con "<numeroDocumento>" se encuentra "<estado>" y se puede autenticar
    And el cliente con "<numeroDocumento>" no se encuentra bloqueado en la autenticacion identity-auth
    When el cliente con numero de documento "<numeroDocumento>" envia una peticion de autenticacion con error debido a que el selfie no tiene "<estado1>" a identity-auth
    Then se valida que el cliente con DNI "<numeroDocumento>" tiene como respuesta del nuevo servicio de autenticacion el codigo de error  "<codigoError>" con mensaje de error "<mensajeError>"
    And el contador de bloqueo de autenticacion del cliente con DNI "<numeroDocumento>" no aumenta

    Examples:
      | numeroDocumento | estado   | canal         | codigoError | mensajeError                                                         | estado1 |
      | 73006406        | ENROLLED | CANAL_EXTERNO | 13.02.16    | Bad request bfa service for passive Liveness validation FaceNotFound | rostro  |

  @IdentityNewAuthenticationClienteErrorRostroMuyCercano
  Scenario Outline: Error en la autenticacion de un cliente porque en la foto del selfie se detecto un rostro muy cerca.
  Se espera conseguir el código de error 13.02.18 al no pasar la prueba de vida, el código de error no se
  contabiliza como un motivo de bloqueo.

    Given "<canal>" es un app registrado en OpenBanking
    And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los enrolamiento identity-auth
    And el cliente con "<numeroDocumento>" se encuentra "<estado>" y se puede autenticar
    And el cliente con "<numeroDocumento>" no se encuentra bloqueado en la autenticacion identity-auth
    When el cliente con numero de documento "<numeroDocumento>" envia una peticion de autenticacion con error debido a que en la foto del selfie se detecto un "<estado1>" a identity-auth
    Then se valida que el cliente con DNI "<numeroDocumento>" tiene como respuesta del nuevo servicio de autenticacion el codigo de error  "<codigoError>" con mensaje de error "<mensajeError>"
    And el contador de bloqueo de autenticacion del cliente con DNI "<numeroDocumento>" no aumenta

    Examples:
      | numeroDocumento | estado   | canal         | codigoError | mensajeError                                                         | estado1       |
      | 73006406        | ENROLLED | CANAL_EXTERNO | 13.02.18    | Bad request bfa service for passive Liveness validation FaceTooClose | rostroCercano |

  @IdentityNewAuthenticationClienteErrorRostroMuyCercaBorde
  Scenario Outline: Error en la autenticacion de un cliente porque en la foto del selfie se detecto un rostro muy cerca al borde.
  Se espera conseguir el código de error 13.02.18 al no pasar la prueba de vida, el código de error no se
  contabiliza como un motivo de bloqueo.

    Given "<canal>" es un app registrado en OpenBanking
    And el cliente con DNI "<numeroDocumento>" no se encuentra bloqueado en los enrolamiento identity-auth
    And el cliente con "<numeroDocumento>" se encuentra "<estado>" y se puede autenticar
    And el cliente con "<numeroDocumento>" no se encuentra bloqueado en la autenticacion identity-auth
    When el cliente con numero de documento "<numeroDocumento>" envia una peticion de autenticacion con error debido a que se detecto un rostro "<estado1>" a identity-auth
    Then se valida que el cliente con DNI "<numeroDocumento>" tiene como respuesta del nuevo servicio de autenticacion el codigo de error  "<codigoError>" con mensaje de error "<mensajeError>"
    And el contador de bloqueo de autenticacion del cliente con DNI "<numeroDocumento>" no aumenta

    Examples:
      | numeroDocumento | estado   | canal         | codigoError | mensajeError                                                                 | estado1    |
      | 73006406        | ENROLLED | CANAL_EXTERNO | 13.02.19    | Bad request bfa service for passive Liveness validation FaceTooCloseToBorder | CercaBorde |
