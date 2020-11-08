package run.mycode.basiclti.service;

import run.mycode.basiclti.persistence.model.LtiKey;
import run.mycode.basiclti.persistence.model.User;

/**
 * A service associating consumer keys with the secret or the user that owns it
 * @author bdahl
 */
public interface KeyService  {
    /**
     * Retrieve the consumer secret associated with that consumer's key
     * @param key the key used to identify a consumer
     * @return the shared secret used to sign messages to/from the consumer
     */
    public String getSecretForKey(String key);
    
    /**
     * Retrieve a key object containing the owner and the signing secret
     * identified by the key
     * @param key the key used to identify a consumer
     * @return the key object associated with the consumer
     */
    public LtiKey getKeyInfo(String key);
    
    /**
     * Retrieve the user who owns a given consumer key
     * @param key the key used to identify a consumer
     * @return the user who owns the given key
     */
    public User getUserForKey(String key);
}
