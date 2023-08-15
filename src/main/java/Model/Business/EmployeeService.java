package Model.Business;

import Model.Contract.Employee;

import java.io.IOException;
import java.util.List;

public interface EmployeeService {
    List<Employee> getList(int id) throws IOException;

    int createEmployee(Employee employee);
    Employee getEmployeeById(int id);
    Employee editEmployee(int id);
}
