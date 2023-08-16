package Model.Contract;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class CreateEmployeeWithOnlyReqFields {
    private int id;
    private String firstName;
    private String lastName;
    private int companyId;
    private boolean isActive;

    public CreateEmployeeWithOnlyReqFields(int id, String firstName, String lastName, int companyId, boolean isActive) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyId = companyId;
        this.isActive = isActive;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public void setIsActive(boolean active) {
        this.isActive = active;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getCompanyId() {
        return companyId;
    }

    public boolean getIsActive() {
        return isActive;
    }
}
