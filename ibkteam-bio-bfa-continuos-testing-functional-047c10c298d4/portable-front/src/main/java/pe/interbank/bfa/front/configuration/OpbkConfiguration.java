package pe.interbank.bfa.front.configuration;

public class OpbkConfiguration {
    public static String getOpbkBaseUrl() {
        return (String) PropertyManager.getInstance().get("opbk.base.url");
    }
    public static String getUrlAuth2() {
        return (String) PropertyManager.getInstance().get("identity.auth.base.url");
    }

    public static String getAuthSecurityHeader() {
        return (String) PropertyManager.getInstance().get("opbk.authorization.security.header");
    }

    public static String getIdentitySubscriptionKey() {
        return (String) PropertyManager.getInstance().get("opbk.authorization.identity.subscriptionKey");
    }

    public static String getIdentity2SubscriptionKey(){
        return (String) PropertyManager.getInstance().get("opbk.authorization.identity2.subscriptionKey");
    }
}
