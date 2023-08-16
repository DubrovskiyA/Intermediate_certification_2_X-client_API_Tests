package Test;

import Ext.AuthResolver;
import Ext.CompanyProvider;
import Client.EmployeeClient;
import Client.EmployeeClientImpOkHttp;
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
    private String URL ="https://x-clients-be.onrender.com/employee";
    EmployeeClient client;
    @BeforeEach
    public void SetUp(){
        client =new EmployeeClientImpOkHttp(URL);
    }
    @Test
    @DisplayName("Создание сотрудника (4 минимально необходимых поля в теле запроса!)")
    public void creatingEmployee(Company company, UserInfo info) throws IOException {
//        Создаем компанию
        int companyId=company.getId();
//        Запрашиваем список сотрудников этой компании
        List<Employee> listBefore=client.getList(companyId);
//        Создаем сотрудника
        CreateEmployee employee=new CreateEmployee("Bill","Lum",companyId,"89675556677");
        int id=client.createEmployee(employee,info.getUserToken());
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
    @DisplayName("Получить список сотрудников компании")
    public void gettingEmployeesOfCompany(Company company, UserInfo info) throws IOException {
//        Создаем компанию
        int companyId=company.getId();
//        Запрашиваем список сотрудников этой компании
        List<Employee> listBefore=client.getList(companyId);
//        Создаем сотрудника
        CreateEmployee employee=new CreateEmployee("Bill","Lum",companyId,"89675556677");
        client.createEmployee(employee,info.getUserToken());
        client.createEmployee(employee,info.getUserToken());
        client.createEmployee(employee,info.getUserToken());
//        Запрашиваем список сотрудников этой компании
        List<Employee> listAfter=client.getList(companyId);
        assertEquals(3,listAfter.size()-listBefore.size());
    }
    @Test
    @DisplayName("Получить сотрудника по id")
    public void gettingEmployeeById(Company company, UserInfo info) throws IOException {
//        Создаем компанию
        int companyId=company.getId();
//        Создаем сотрудника
        CreateEmployee employee=new CreateEmployee("Bill","Lum",companyId,"89675556677");
        int id=client.createEmployee(employee,info.getUserToken());
//        Запрашиваем сотрудника по id
        Employee employee1=client.getEmployeeById(id);
        assertEquals(employee.getFirstName(),employee1.getFirstName());
        assertEquals(employee.getLastName(),employee1.getLastName());
        assertEquals(employee.getCompanyId(),employee1.getCompanyId());
        assertEquals(employee.getPhone(),employee1.getPhone());
        assertEquals(companyId,employee1.getCompanyId());
    }
    @Test
    @DisplayName("Изменить информацию о сотруднике")
    public void updatingEmployee(Company company, UserInfo info) throws IOException {
//        Создаем компанию
        int companyId=company.getId();
//        Создаем сотрудника
        CreateEmployee employee=new CreateEmployee("Bill","Lum",companyId,"89675556677");
        int id=client.createEmployee(employee,info.getUserToken());
//        Обновляем информацию о сотруднике
        UpdateEmployee employeeToUpdate=
                new UpdateEmployee("Jeff","asdfg@tmail.com","http://catava.org",false);
        client.updateEmployee(id,employeeToUpdate,info.getUserToken());
//        Запрашиваем сотрудника по id
        Employee employee1=client.getEmployeeById(id);

        assertEquals(employeeToUpdate.getLastName(),employee1.getLastName());
        assertEquals(employeeToUpdate.getEmail(),employee1.getEmail());
        assertEquals(employeeToUpdate.getUrl(),employee1.getUrl());
        assertEquals(employeeToUpdate.getIsActive(),employee1.getIsActive());
    }
}
