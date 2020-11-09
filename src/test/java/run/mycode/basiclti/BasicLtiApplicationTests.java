package run.mycode.basiclti;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

class BasicLtiApplicationTests {
    
    private static final Logger LOG = LogManager.getLogger(BasicLtiApplicationTests.class);

    @Test
    void nullTest() {
        LOG.warn("THIS IS NOT A TEST!!!");
    }
    
    //TODO: Need to test LTI Verification. how to mock an http connection for filter?
}
