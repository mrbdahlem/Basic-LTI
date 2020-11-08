package run.mycode.basiclti.service;

import run.mycode.basiclti.authentication.LtiKey;


/**
 * A service associating consumer keys with the secret or the user that owns it
 * @author bdahl
 */
public interface LtiKeyService  {
    /**
     * Retrieve the consumer secret associated with that consumer's key
     * @param key the key used to identify a consumer
     * @return the shared secret used to sign messages to/from the consumer
     */
    public String getSecretForKey(String key);
    
    /**
     * Retrieve stored key data for a consumer key
     * @param key the key to load the data for
     * @return an object pairing consumer key and shared secret
     */
    public LtiKey getKey(String key);
}
