package pe.interbank.bfa.front.utils;

public class Constants {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String SUBSCRIPTION_KEY_HEADER = "Ocp-Apim-Subscription-Key";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String UPDATED_BY_HEADER = "X-Updated-By";
    public static final String X_API_VERSION_HEADER = "X-Api-Version";
    public static final String X_USER_RENIEC_HEADER = "X-User-Id-Reniec";
    public static final String OPBK_TOKEN_ENV = "TOKEN_OPBK";
    public static final String CHANNEL_ENV = "CHANNEL";
    public static final String OCR_KEY_ENV = "OCR_KEY";
    public static final String DOCUMENT_TYPE_PARAM = "identityDocumentType";
    public static final String DOCUMENT_NUMBER_PARAM = "identityDocumentNumber";
    public static final String COMPANY_PARAM = "identityCompany";
    public static final String CHANNEL_PARAM = "identityChannel";
    public static final String CUSTOMER_CODE = "code";

    public static final String DOCUMENT_TYPE_DNI = "DNI";
    public static final String COMPANY_INTERBANK = "INTERBANK";
    public static final String CURRENT_FAILED_ATTEMPTS = "currentFailedAttempts";
}
