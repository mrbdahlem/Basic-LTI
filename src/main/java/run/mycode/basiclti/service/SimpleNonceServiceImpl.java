package run.mycode.basiclti.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An in-memory nonce registration and validation service
 * 
 * @author dahlem.brian
 */
public class SimpleNonceServiceImpl implements NonceService {
    private final Map<String, List<Nonce>> nonces;
    private final long window;
    
    /**
     * Create a service to ensure that no consumer attempts to use a nonce more
     * than once within a given time window
     * 
     * @param windowSeconds the number of seconds within a nonce cannot be
     *                      repeated (half before, half after current time)
     */
    public SimpleNonceServiceImpl(long windowSeconds) {
        nonces = new HashMap<>();
        this.window = windowSeconds;
    }
    
    /**
     * Create a service to ensure that no consumer attempts to use a nonce more
     * than once within a 10 minute window (5 minutes before, 5 after the 
     * current time)
     */
    public SimpleNonceServiceImpl() {
        this(600);
    }

    @Override
    public void validateNonce(String consumerkey, String nonce, long timestamp) {
        // Determine the valid time window for the timestamp
        long now = System.currentTimeMillis() / 1000L;
        long after = now - (window / 2);
        long before = now + (window / 2);
        
        // If the timestamp is outside of the window, the request is invalid
        if (timestamp < after || timestamp > before) {
            throw new InvalidNonceException("Invalid request timestamp");
        }
        
        // Get the list of nonces 
        List<Nonce> consumernonces = getNonces(consumerkey, after);
                
        // If the nonce has already been used, the request is invalid
        if (consumernonces.stream().anyMatch(n -> n.matches(nonce))) {
            throw new InvalidNonceException("Duplicate nonce");
        }
        
        // Add the checked nonce so it can't be used again
        consumernonces.add(new Nonce(nonce, timestamp));
    }
    
    /**
     * Get the list of nonces that have been used by a particular consumer after
     * a particular timestamp
     * 
     * @param key an identifier unique to the particular consumer
     * @param afterTime the timestamp after which nonces still need to be 
     *                  checked
     * @return a list of nonces used by the specified consumer after the given
     *         timestamp or an empty list if the consumer hasn't made a request
     *         in the given window
     */
    private List<Nonce> getNonces(String key, long afterTime) {
        
        // Get the list of nonces for the given key
        List<Nonce> consumernonces = nonces.get(key);
        
        if (consumernonces == null) {
            // If there wasn't a list, create one
            consumernonces = new ArrayList<>();
            nonces.put(key, consumernonces);
        }
        else {
            // If there was a list, purge any nonces before the given timestamp
            consumernonces.removeIf(nonce -> nonce.before(afterTime));
        }
        
        return consumernonces;
    }
    
    private static class Nonce {
        public final String nonce;
        public final long timestamp;
        
        public Nonce(String nonce, long timestamp) {
            this.nonce = nonce;
            this.timestamp = timestamp;
        }
        
        public boolean matches(String nonce) {
            return this.nonce.equals(nonce);
        }
        
        public boolean before(long timestamp) {
            return this.timestamp < timestamp;
        }
    }
}
