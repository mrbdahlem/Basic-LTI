package run.mycode.basicltitest.persistence.model;

/**
 * Basic information associating a consumer key with the signing secret and user
 * that administers the key
 * 
 * @author dahlem.brian
 */
public class LtiKey {
    private String key;
    private String secret;
    private User owner;

    public LtiKey(String key, String secret, User owner) {
        this.key = key;
        this.secret = secret;
        this.owner = owner;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
