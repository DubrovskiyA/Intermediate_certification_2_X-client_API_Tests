package Model.Business;

import Model.Employee;

import java.io.IOException;
import java.util.List;

public interface EmployeeClient {
    List<Employee> getList(int id) throws IOException;

    int createEmployee(Employee employee);
    Employee getEmployeeById(int id) throws IOException;
    Employee editEmployee(int id);
}
