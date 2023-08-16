package Model.Business;

public class UpdateEmployee {
    private String lastName;
    private String email;
    private String url;
    private boolean isActive;

    public UpdateEmployee(String lastName, String email, String url, boolean isActive) {
        this.lastName = lastName;
        this.email = email;
        this.url = url;
        this.isActive = isActive;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }
}
