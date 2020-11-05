package run.mycode.basicltitest.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

/**
 *
 * @author dahlem.brian
 */
@Service
public class NonceServiceImpl implements NonceService {
    private final List<Nonce> nonces;
    private final long window;
    
    public NonceServiceImpl() {
        nonces = new ArrayList<>();
        window = 600;
    }

    @Override
    public void validateNonce(String consumerkey, String nonce, long timestamp) {
        cleanup();
        
        long now = System.currentTimeMillis() / 1000L;
        long before = now - (window / 2);
        long after = now + (window / 2);
        
        if (timestamp < before || timestamp > after) {
            throw new BadCredentialsException("Invalid request timestamp");
        }
        
        if (nonces.stream().anyMatch(n -> n.matches(consumerkey, nonce))) {
            throw new BadCredentialsException("Duplicate nonce");
        }
        
        nonces.add(new Nonce(consumerkey, nonce, timestamp));
    }
    
    private void cleanup() {
        long now = System.currentTimeMillis() / 1000L;
        long before = now - (window / 2);
        
        nonces.removeIf(nonce -> nonce.timestamp < before);
    }
    
    private static class Nonce {
        public String nonce;
        public long timestamp;
        public String key;
        
        public Nonce(String key, String nonce, long timestamp) {
            this.key = key;
            this.nonce = nonce;
            this.timestamp = timestamp;
        }
        
        public boolean matches(String key, String nonce) {
            return this.key.equals(key) && this.nonce.equals(nonce);
        }
    }
}
