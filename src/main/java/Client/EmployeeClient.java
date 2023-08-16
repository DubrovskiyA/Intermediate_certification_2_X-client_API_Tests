package Client;

import Model.Business.CreateEmployee;
import Model.Business.UpdateEmployee;
import Model.Employee;

import java.io.IOException;
import java.util.List;

public interface EmployeeClient {
    List<Employee> getList(int id) throws IOException;
    int createEmployee(CreateEmployee employee, String userToken) throws IOException;
    Employee getEmployeeById(int id) throws IOException;
    int updateEmployee(int id, UpdateEmployee employee, String userToken) throws IOException;
}
