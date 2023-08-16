package Model.Business;

import java.util.Objects;

public class CreateEmployee {
    private String firstName;
    private String lastName;
    private int companyId;
    private String phone;

    public CreateEmployee(String firstName, String lastName, int companyId, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyId = companyId;
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreateEmployee that)) return false;
        return getCompanyId() == that.getCompanyId() && Objects.equals(getFirstName(), that.getFirstName()) && Objects.equals(getLastName(), that.getLastName()) && Objects.equals(getPhone(), that.getPhone());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getCompanyId(), getPhone());
    }
}
