package run.mycode.basiclti.persistence.model;

/**
 * A sample user - this would be an instructor/admin that adds this tool to
 * an LMS
 * 
 * @author dahlem.brian
 */
public class User {
    private String name;
    private String organization;

    public User(String name, String organization) {
        this.name = name;
        this.organization = organization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
