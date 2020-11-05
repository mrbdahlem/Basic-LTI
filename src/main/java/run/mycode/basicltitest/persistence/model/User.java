package run.mycode.basicltitest.persistence.model;

/**
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
