package run.mycode.basiclti.authentication;

/**
 * Basic information associating a consumer key with the signing secret
 * 
 * @author dahlem.brian
 */
public abstract class LtiKey {
    private String key;
    private String secret;

    public LtiKey(String key, String secret) {
        this.key = key;
        this.secret = secret;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
