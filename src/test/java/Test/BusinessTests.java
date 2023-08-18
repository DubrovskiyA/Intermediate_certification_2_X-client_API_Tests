package Test;

import Auth.AuthorizeService;
import Client.CompanyService;
import Ext.AdminAuthorized;
import Ext.AuthResolver;
import Client.EmployeeClient;
import Client.EmployeeClientImpOkHttp;
import Ext.ClientAuthorized;
import Ext.CompanyProvider;
import Ext.props.PropertyProvider;
import Model.Business.CreateEmployee;
import Model.Business.UpdateEmployee;
import Model.Company;
import Model.Employee;
import Model.UserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({CompanyProvider.class, AuthResolver.class})
public class BusinessTests {
    private final static String BASE_URL = PropertyProvider.getInstance().getProps().getProperty("test.url");
    EmployeeClient client;
    @BeforeEach
    public void SetUp(){
        client =new EmployeeClientImpOkHttp(BASE_URL);
    }
    @Test
    @DisplayName("Создание сотрудника авторизованным пользователем(admin)(4 минимально необходимых поля в теле запроса!" +
            "(firstName,LastName,companyId,phone) НЕ СООТВЕТСТВУЕТ ДОКУМЕНТАЦИИ)")
    public void adminAuthorizationCreatingEmployee(CompanyService service, @AdminAuthorized AuthorizeService authService) throws IOException {
//        Создаем компанию
        int companyId=service.createCompany();

//        Запрашиваем список сотрудников этой компании
        List<Employee> listBefore=client.getList(companyId);

//        Создаем сотрудника
        CreateEmployee employee=new CreateEmployee("Bill","Lum",companyId,"89675556677");
        int id=client.createEmployee(employee, authService.auth());

//        Запрашиваем список сотрудников этой компании
        List<Employee> listAfter=client.getList(companyId);
        assertEquals(1,listAfter.size()-listBefore.size());

//        Запрашиваем сотрудника по id
        Employee employee1=client.getEmployeeById(id);
        assertEquals(employee.getFirstName(),employee1.getFirstName());
        assertEquals(employee.getLastName(),employee1.getLastName());
        assertEquals(employee.getCompanyId(),employee1.getCompanyId());
        assertEquals(employee.getPhone(),employee1.getPhone());
    }
    @Test
    @DisplayName("Создание сотрудника авторизованным пользователем(client)(4 минимально необходимых поля в теле запроса!" +
            "(firstName,LastName,companyId,phone) НЕ СООТВЕТСТВУЕТ ДОКУМЕНТАЦИИ)")
    public void clientAuthorizationCreatingEmployee(CompanyService service, @ClientAuthorized AuthorizeService authService) throws IOException {
//        Создаем компанию
        int companyId=service.createCompany();

//        Запрашиваем список сотрудников этой компании
        List<Employee> listBefore=client.getList(companyId);

//        Создаем сотрудника
        CreateEmployee employee=new CreateEmployee("Bill","Lum",companyId,"89675556677");
        int id=client.createEmployee(employee, authService.auth());

//        Запрашиваем список сотрудников этой компании
        List<Employee> listAfter=client.getList(companyId);
        assertEquals(1,listAfter.size()-listBefore.size());

//        Запрашиваем сотрудника по id
        Employee employee1=client.getEmployeeById(id);
        assertEquals(employee.getFirstName(),employee1.getFirstName());
        assertEquals(employee.getLastName(),employee1.getLastName());
        assertEquals(employee.getCompanyId(),employee1.getCompanyId());
        assertEquals(employee.getPhone(),employee1.getPhone());
    }
    @Test
    @DisplayName("Неавторизованный пользователь не может создать сотрудника")
    public void unauthorizedUserShouldNotCreatingEmployee(CompanyService service) throws IOException {
//        Создаем компанию
        int companyId=service.createCompany();

//        Запрашиваем список сотрудников этой компании
        List<Employee> listBefore=client.getList(companyId);

//        Создаем сотрудника
        CreateEmployee employee=new CreateEmployee("Bill","Lum",companyId,"89675556677");
        int id=client.unauthorizedCreateEmployee(employee);

//        Запрашиваем список сотрудников этой компании
        List<Employee> listAfter=client.getList(companyId);
        assertEquals(0,listAfter.size()-listBefore.size());
    }
    @Test
    @DisplayName("Получение списка сотрудников компании")
    public void gettingEmployeesOfCompany(CompanyService service, @AdminAuthorized AuthorizeService authService) throws IOException {
//        Создаем компанию
        int companyId=service.createCompany();
//        Запрашиваем список сотрудников этой компании
        List<Employee> listBefore=client.getList(companyId);
//        Создаем сотрудника
        CreateEmployee employee=new CreateEmployee("Bill","Lum",companyId,"89675556677");
        client.createEmployee(employee,authService.auth());
        client.createEmployee(employee,authService.auth());
        client.createEmployee(employee,authService.auth());
//        Запрашиваем список сотрудников этой компании
        List<Employee> listAfter=client.getList(companyId);
        assertEquals(3,listAfter.size()-listBefore.size());
    }
    @Test
    @DisplayName("Получение сотрудника по id")
    public void gettingEmployeeById(CompanyService service, @AdminAuthorized AuthorizeService authService) throws IOException {
//        Создаем компанию
        int companyId=service.createCompany();
//        Создаем сотрудника
        CreateEmployee employee=new CreateEmployee("Bill","Lum",companyId,"89675556677");
        int id=client.createEmployee(employee,authService.auth());
//        Запрашиваем сотрудника по id
        Employee employee1=client.getEmployeeById(id);
        assertEquals(employee.getFirstName(),employee1.getFirstName());
        assertEquals(employee.getLastName(),employee1.getLastName());
        assertEquals(employee.getCompanyId(),employee1.getCompanyId());
        assertEquals(employee.getPhone(),employee1.getPhone());
        assertEquals(companyId,employee1.getCompanyId());
    }
    @Test
    @DisplayName("Изменение информации о сотруднике авторизованным пользователем(admin)")
    public void adminUpdatingEmployee(CompanyService service, @AdminAuthorized AuthorizeService authService) throws IOException {
//        Создаем компанию
        int companyId=service.createCompany();
//        Создаем сотрудника
        CreateEmployee employee=new CreateEmployee("Bill","Lum",companyId,"89675556677");
        int id=client.createEmployee(employee, authService.auth());
//        Обновляем информацию о сотруднике
        UpdateEmployee employeeToUpdate=
                new UpdateEmployee("Jeff","asdfg@tmail.com","http://catava.org",false);
        client.updateEmployee(id,employeeToUpdate, authService.auth());
//        Запрашиваем сотрудника по id
        Employee employee1=client.getEmployeeById(id);

        assertEquals(employeeToUpdate.getLastName(),employee1.getLastName());
        assertEquals(employeeToUpdate.getEmail(),employee1.getEmail());
        assertEquals(employeeToUpdate.getUrl(),employee1.getUrl());
        assertEquals(employeeToUpdate.getIsActive(),employee1.getIsActive());
    }
    @Test
    @DisplayName("Изменение информации о сотруднике авторизованным пользователем(client)")
    public void clientUpdatingEmployee(CompanyService service, @ClientAuthorized AuthorizeService authService) throws IOException {
//        Создаем компанию
        int companyId=service.createCompany();
//        Создаем сотрудника
        Employee employee=new Employee(1,"Bill","Lum","Pol",companyId,
                "qweq@dfgs.op","http://wef.po","89675556677","19995-09-08",true);
        int id=client.createEmployeeWithAllFields(employee, authService.auth());
//        Обновляем информацию о сотруднике
        UpdateEmployee employeeToUpdate=
                new UpdateEmployee("Jeff","asdfg@tmail.com","http://catava.org",false);
        client.updateEmployee(id,employeeToUpdate, authService.auth());
//        Запрашиваем сотрудника по id
        Employee employee1=client.getEmployeeById(id);

        assertEquals(employeeToUpdate.getLastName(),employee1.getLastName());
        assertEquals(employeeToUpdate.getEmail(),employee1.getEmail());
        assertEquals(employeeToUpdate.getUrl(),employee1.getUrl());
        assertEquals(employeeToUpdate.getIsActive(),employee1.getIsActive());
    }
    @Test
    @DisplayName("Неавторизованный пользователь не может изменить информацию о сотруднике")
    public void unauthorizedUserShouldNotUpdatingEmployee(CompanyService service, @AdminAuthorized AuthorizeService authService) throws IOException {
//        Создаем компанию
        int companyId=service.createCompany();
//        Создаем сотрудника
        Employee employee=new Employee(1,"Bill","Lum","Pol",companyId,
                "qweq@dfgs.op","http://wef.po","89675556677","19995-09-08",true);
        int id=client.createEmployeeWithAllFields(employee, authService.auth());
//        Обновляем информацию о сотруднике
        UpdateEmployee employeeToUpdate=
                new UpdateEmployee("Jeff","asdfg@tmail.com","http://catava.org",false);
        client.unauthorizedUpdateEmployee(id,employeeToUpdate);
//        Запрашиваем сотрудника по id
        Employee employeeBack=client.getEmployeeById(id);

        assertEquals(employee.getLastName(),employeeBack.getLastName());
        assertEquals(employee.getEmail(),employeeBack.getEmail());
        assertEquals(employee.getUrl(),employeeBack.getUrl());
        assertEquals(employee.getIsActive(),employeeBack.getIsActive());
    }
}
