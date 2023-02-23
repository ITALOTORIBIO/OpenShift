package pe.interbank.bfa.front.utils;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import pe.interbank.bfa.front.configuration.OpbkConfiguration;

import static io.restassured.RestAssured.given;
import static pe.interbank.bfa.front.utils.Constants.*;

public class Security {

    public static String createAccessTokenOPBK() {
        var body = String.format("grant_type=%s&scope=%s&", "client_credentials", "token:application");
        try {
            return "Bearer " + given()
                    .header(AUTHORIZATION_HEADER, OpbkConfiguration.getAuthSecurityHeader())
                    .contentType(ContentType.URLENC)
                    .body(body)
                    .post(OpbkConfiguration.getOpbkBaseUrl() + "/security/v1/oauth/token")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .extract()
                    .response()
                    .jsonPath()
                    .getString("access_token");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
