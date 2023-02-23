## BFA-BIOMETRIA
### bfa-continuous-testing-functional
Proyecto que recopila las regresiones automatizadas, las ejecuta en un AzureDevops pipeline y genera un reporte descargable

### Requisitos

- Tener instalado **JDK 8 o superior**
- Tener instalado **última versión** de **Maven** **3.8.6**

### Ejecución local usando Maven

Abrir una ventana de comando de windows (cmd) y ejecutar:

```
mvn clean verify "-Dcucumber.filter.tags=@regresion" "-Denvironment=uat"
```
Donde el tag "@regresion" debe ser reemplazado por el tag del escenario que se necesita ejecutar

### Generación de reporte local usando Maven

Luego de haber ejecutado una regresion se utiliza el siguiente comando para generar un reporte:
```
mvn serenity:aggregate
```
Al finalizar la ejecución del comando se puede abrir el reporte usando en la ruta que se muestra en la terminal

```
[INFO] SERENITY REPORTS
[INFO]   - Full Report: file:///C:/dev/ibk-team-repo/bfa-continuous-testing-funtional/target/site/serenity/index.html
```
## NewAuthentication
| TAG LARGO | DESCRIPCIÓN                                                                                                                                                                                                 |
|-----------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| @IdentityNewAuthentication| Tag para ejecutar todo el feature del api v2 de autenticación.                                                                                                                                              |
| @NewAuthenticationClientEnrolled| Tag para ejecutar el escenario de autenticación exitosa de cliente enrolado con DNI y desde el api modularizado de autenticación.                                                                           |  
| @IdentityNewAuthenticationClienteErrorFraude| Tag para ejecutar el escenario de autenticación fallida de cliente enrolado con DNI por error de fraude en el selfie y desde el api modularizado de autenticación.                                          |
|@IdentityNewAuthenticationClienteErrorImagenRecortada| Tag para ejecutar el escenario de autenticación fallida de cliente enrolado con DNI por error al tomar la foto del selfie recortada y desde el api modularizado de autenticación.                           |
|@IdentityNewAuthenticationClienteErrorMuchosRostros| Tag para ejecutar el escenario de autenticación fallida de cliente enrolado con DNI por error al detectar muchos rostros en la foto del selfie y desde el api modularizado de autenticación.                |
|@IdentityNewAuthenticationClienteErrorMask| Tag para ejecutar el escenario de autenticación fallida de cliente enrolado con DNI por error que tomarse la foto del selfie con una mascarilla y desde el api modularizado de autenticación.               |
|@IdentityNewAuthenticationClienteErrorBajaCalidad| Tag para ejecutar el escenario de autenticación fallida de cliente enrolado con DNI con error por imagen de baja calidad y desde el api modularizado de autenticación.                                      |
|@IdentityNewAuthenticationClienteErrorNoDetectaRostro| Tag para ejecutar el escenario de autenticación fallida de cliente enrolado con DNI con error por no detectar un rostro en el selfie y desde el api modularizado de autenticación.                          |
|@IdentityNewAuthenticationClienteErrorRostroMuyCercano| Tag para ejecutar el escenario de autenticación fallida de cliente enrolado con DNI con error por no detectar un rostro muy cercano en el selfie y desde el api modularizado de autenticación.              |
|@IdentityNewAuthenticationClienteErrorRostroMuyCercaBorde| Tag para ejecutar el escenario de autenticación fallida de cliente enrolado con DNI con error por no detectar un rostro muy cercano a los bordes en el selfie y desde el api modularizado de autenticación. |
