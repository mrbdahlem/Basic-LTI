package run.mycode.basicltitest.service;

/**
 * A service to validate that a nonce hasn't been used by a particular consumer
 * within a given time window
 * 
 * @author dahlem.brian
 */
public interface NonceService {
    public void validateNonce(String consumerId, String nonce, long timestamp);
}
