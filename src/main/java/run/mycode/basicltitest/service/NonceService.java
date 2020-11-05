package run.mycode.basicltitest.service;

/**
 *
 * @author dahlem.brian
 */
public interface NonceService {
    public void validateNonce(String consumerkey, String nonce, long timestamp);
}
