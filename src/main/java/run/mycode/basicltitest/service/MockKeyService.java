package run.mycode.basicltitest.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imsglobal.aspect.LtiKeySecretService;
import org.springframework.stereotype.Service;

@Service
public class MockKeyService implements KeyService {
    private static final Logger LOG = LogManager.getLogger(MockKeyService.class);
    
    public MockKeyService() {
        LOG.info("Mock Key Service Initialized");
    }
    
    @Override
    public String getSecretForKey(String key) {
        LOG.info("Secret Request for Key: " + key);
        return "secretkey";
    }
}