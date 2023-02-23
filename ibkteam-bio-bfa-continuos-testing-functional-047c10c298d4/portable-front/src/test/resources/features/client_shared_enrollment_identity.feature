Feature: Enrolamiento compartido

  Scenario: Enrolamiento compartido exitoso
    Se espera conseguir que un cliente con DNI pueda reutilizar un enrolamiento previo que el
    cliente haya realizado por otro canal, el cual se encuentra en el mismo grupo compartido.

    Given "<canal>" es un app registrado en OpenBanking
    And el cliente con numeroDocumento se encuentra enrolado en canal tunki

