package run.mycode.basicltitest.service;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import run.mycode.basicltitest.persistence.model.LtiKey;
import run.mycode.basicltitest.persistence.model.User;

@Service
public class MockKeyService implements KeyService {
    private static final Logger LOG = LogManager.getLogger(MockKeyService.class);
    
    private final List<LtiKey> keys;
    
    public MockKeyService() {
        keys = new ArrayList<>();
        
        // Build 10 fake users with key/secret pairs
        for(int i = 0; i < 10; i++) {
            User u = new User(String.format("USER%03d", i),
                        String.format("ORG%03d", i));
            System.out.println(String.format("KEY%05d", i));
            keys.add(new LtiKey(String.format("KEY%05d", i), "secretkey", u));
        }
        
        LOG.info("Mock Key Service Initialized");
    }
    
    @Override
    public String getSecretForKey(String key) {
        LOG.info("Secret Request for Key: " + key);
        LtiKey found = keys.stream()
                .filter(k -> k.getKey().equals(key))
                .findFirst()
                .orElse(null);
        
        if (found == null) {
            LOG.info(key + " NOT Found."); 
            return null;
        }        
        
        return found.getKey();
    }

    @Override
    public LtiKey getKeyInfo(String key) {
        LOG.info("Info Request for Key: " + key);
        LtiKey found = keys.stream()
                .filter(k -> k.getKey().equals(key))
                .findFirst()
                .orElse(null);
        
        if (found == null) {
            LOG.info(key + " NOT Found.");      
            return null;
        }        
        
        return found;
    }

    @Override
    public User getUserForKey(String key) {        
        LOG.info("User Request for Key: " + key);
        User found = keys.stream()
                .filter(k -> k.getKey().equals(key))
                .map(k -> k.getOwner())
                .findFirst()
                .orElse(null);
        
        if (found == null) {
            LOG.info(key + " NOT Found.");  
            return null;
        }        
        
        return found;
    }
}