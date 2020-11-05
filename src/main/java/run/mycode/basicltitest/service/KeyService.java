package run.mycode.basicltitest.service;

import run.mycode.basicltitest.persistence.model.LtiKey;
import run.mycode.basicltitest.persistence.model.User;

public interface KeyService  {
    public String getSecretForKey(String key);
    public LtiKey getKeyInfo(String key);
    public User getUserForKey(String key);
}
