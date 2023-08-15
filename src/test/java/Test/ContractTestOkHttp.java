package Test;

import Ext.LogInterceptor;
import Model.Contract.Company;
import Model.Contract.Employee;
import Model.Contract.UserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContractTestOkHttp {
    private String BASE_URL ="https://x-clients-be.onrender.com";
    private String PATH_AUTH="auth/login";
    private String PATH_COMPANY ="company";
    private String PATH_EMPLOYEE ="employee";
    ObjectMapper mapper=new ObjectMapper();
    OkHttpClient client;
    MediaType APPLICATION_JSON=MediaType.parse("application/json; charset=utf-8");
    @BeforeEach
    public void setUp(){
        client=new OkHttpClient.Builder().addNetworkInterceptor(new LogInterceptor()).build();
    }
    @Test
    @DisplayName("Проверяем, что можно получить список сотрудников компании, статус-код 200, заголовок content-type")
    public void shouldReceive200OnGetRequest() throws IOException {
        HttpUrl url=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH_EMPLOYEE).addQueryParameter("company",Integer.toString(1)).build();
        Request request=new Request.Builder().get().url(url).build();
        Response response=client.newCall(request).execute();
        String body=response.body().string();
        assertEquals(200,response.code());
        assertEquals(1,response.headers("content-type").size());
        assertEquals("application/json; charset=utf-8",response.header("content-type"));
        assertTrue(body.startsWith("["));
        assertTrue(body.endsWith("]"));
    }
    @Test
    @DisplayName("Проверяем, что можно создать сотрудника только с обязательными полями в теле запроса, статус-код 201")
    public void shouldReceive201OnCreateEmployeeWithRequiredFields() throws IOException {
        String userToken=Auth();
//        Создание компании
        int idCreatedCompany = createCompany(userToken);
//        Создание сотрудника
        HttpUrl url=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH_EMPLOYEE).build();
        RequestBody body=RequestBody.create(mapper.writeValueAsString(new Employee
                (1,"Bill","Smith",idCreatedCompany,true)), APPLICATION_JSON);
        Request request=new Request.Builder().post(body).url(url).addHeader("x-client-token",userToken).build();
        Response response=client.newCall(request).execute();
        assertEquals(201,response.code());
    }
    @Test
    @DisplayName("Проверяем, что можно создать сотрудника со всеми полями в теле запроса, статус-код 201")
    public void shouldReceive201OnCreateEmployeeWithAllFieldsRequest() throws IOException {
        String userToken=Auth();
//        Создание компании
        int idCreatedCompany=createCompany(userToken);
//        Создание сотрудника
        HttpUrl url=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH_EMPLOYEE).build();
        RequestBody body=RequestBody.create(mapper.writeValueAsString(new Employee
                (1,"Игорь",
                        "Егоров",
                        "Валентинович",
                        idCreatedCompany,
                        "test@pitmail.com",
                        "http://qweerty.ru",
                        "89764563826",
                        "1994-04-01",
                        true)), APPLICATION_JSON);
        Request request=new Request.Builder().post(body).url(url).addHeader("x-client-token",userToken).build();
        Response response=client.newCall(request).execute();
        String bodyResp=response.body().string();
        assertEquals(201,response.code());
        assertEquals(1,response.headers("content-type").size());
        assertEquals("application/json; charset=utf-8",response.header("content-type"));
        assertTrue(bodyResp.startsWith("{"));
        assertTrue(bodyResp.endsWith("}"));
    }
    @Test
    @DisplayName("Проверяем, что нельзя создать сотрудника неавторизованному пользователю, статус-код 401")
    public void shouldReceive401OnUnauthorizedCreateEmployeeWithAllFieldsRequest() throws IOException {
        String userToken=Auth();
//        Создание компании
        int idCreatedCompany=createCompany(userToken);
//        Создание сотрудника
        HttpUrl url=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH_EMPLOYEE).build();
        RequestBody body=RequestBody.create(mapper.writeValueAsString(new Employee
                (1,"Игорь",
                        "Егоров",
                        "Валентинович",
                        idCreatedCompany,
                        "test@pitmail.com",
                        "http://qweerty.ru",
                        "89764563826",
                        "1994-04-01",
                        true)), APPLICATION_JSON);
        Request request=new Request.Builder().post(body).url(url).build();
        Response response=client.newCall(request).execute();
        String bodyResp=response.body().string();
        assertEquals(401,response.code());
        assertEquals(1,response.headers("content-type").size());
        assertEquals("application/json; charset=utf-8",response.header("content-type"));
        assertTrue(bodyResp.startsWith("{"));
        assertTrue(bodyResp.endsWith("}"));
    }
    @Test
    @DisplayName("Проверяем, что можно получить сотрудника по его id, статус-код 200, заголовок content-type")
    public void shouldReceive200OnGetEmployeeRequest() throws IOException {
        String userToken=Auth();
//        Создание компании
        int idCreatedCompany=createCompany(userToken);
//        Создание сотрудника
        HttpUrl url=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH_EMPLOYEE).build();
        Employee employeeForPost=new Employee
                (1,"Егор",
                        "Горов",
                        "Игоревич",
                        idCreatedCompany,
                        "test@pitmail.com",
                        "http://qweerty.ru",
                        "89764563826",
                        "1994-09-09",
                        true);
        RequestBody body=RequestBody.create(mapper.writeValueAsString(employeeForPost), APPLICATION_JSON);
        Request request=new Request.Builder().post(body).url(url).addHeader("x-client-token",userToken).build();
        Response response=client.newCall(request).execute();
        Employee newEmployee=mapper.readValue(response.body().string(),Employee.class);
//        Запрос сотрудника по id
        HttpUrl url1=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH_EMPLOYEE).addPathSegments(String.valueOf(newEmployee.getId())).build();
        Request request1=new Request.Builder().get().url(url1).build();
        Response response1=client.newCall(request1).execute();
        Employee newEmployeeCall=mapper.readValue(response1.body().string(),Employee.class);
        assertEquals(200,response1.code());
        assertEquals(1,response.headers("content-type").size());
        assertEquals("application/json; charset=utf-8",response.header("content-type"));
        assertEquals(employeeForPost.getFirstName(),newEmployeeCall.getFirstName());
        assertEquals(employeeForPost.getLastName(),newEmployeeCall.getLastName());
        assertEquals(employeeForPost.getMiddleName(),newEmployeeCall.getMiddleName());
        assertEquals(employeeForPost.getCompanyId(),newEmployeeCall.getCompanyId());
        assertEquals(employeeForPost.getEmail(),newEmployeeCall.getEmail());
        assertEquals(employeeForPost.getUrl(),newEmployeeCall.getUrl());
        assertEquals(employeeForPost.getPhone(),newEmployeeCall.getPhone());
        assertEquals(employeeForPost.getBirthdate(),newEmployeeCall.getBirthdate());
        assertEquals(employeeForPost.isActive(),newEmployeeCall.isActive());
    }
    @Test
    @DisplayName("Проверяем, что нельзя получить несозданного сотрудника по id, статус-код 404, заголовок content-type")
    public void shouldReceive404OnGetUncreatedEmployeeRequest() throws IOException {
        HttpUrl url=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH_EMPLOYEE).addPathSegments(Integer.toString(0)).build();
        Request request=new Request.Builder().get().url(url).build();
        Response response=client.newCall(request).execute();
        assertEquals(404,response.code());
    }
    @Test
    @DisplayName("Проверяем, что можно изменить информацию о сотруднике, статус-код 201")
    public void shouldReceive201OnEditEmployeeRequest() throws IOException {
        String userToken=Auth();
//        Создание компании
        int idCreatedCompany=createCompany(userToken);
//        Создание сотрудника
        HttpUrl url=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH_EMPLOYEE).build();
        Employee employeeForPost=new Employee
                (1,"Игнат",
                        "Воров",
                        "Петрович",
                        idCreatedCompany,
                        "tester@pitmail.com",
                        "http://qw2323eerty.ru",
                        "89764673826",
                        "1987-10-07",
                        true);
        RequestBody body=RequestBody.create(mapper.writeValueAsString(employeeForPost), APPLICATION_JSON);
        Request request=new Request.Builder().post(body).url(url).addHeader("x-client-token",userToken).build();
        Response response=client.newCall(request).execute();
        Employee newEmployee=mapper.readValue(response.body().string(),Employee.class);
//        Изменнение информации о сотруднике
        HttpUrl url1=HttpUrl.parse(BASE_URL).newBuilder()
                .addPathSegment(PATH_EMPLOYEE)
                .addPathSegment(String.valueOf(newEmployee.getId()))
                .build();
        Employee employeeToEdit=new Employee("Будов","testerYO@bugmail.com","http://ytrewq.com","89768765544",false);
        RequestBody body1=RequestBody.create(mapper.writeValueAsString(employeeToEdit),APPLICATION_JSON);
        Request request1=new Request.Builder().patch(body1).url(url1).addHeader("x-client-token",userToken).build();
        Response response1=client.newCall(request1).execute();
        Employee employeeEdited=mapper.readValue(response1.body().string(),Employee.class);
        assertEquals(201,response1.code());
        assertEquals(employeeToEdit.getLastName(),employeeEdited.getLastName());
        assertEquals(employeeToEdit.getEmail(),employeeEdited.getEmail());
        assertEquals(employeeToEdit.getUrl(),employeeEdited.getUrl());
        assertEquals(employeeToEdit.getPhone(),employeeEdited.getPhone());
        assertEquals(employeeToEdit.isActive(),employeeEdited.isActive());
    }
    @Test
    @DisplayName("Проверяем, что нельзя изменить информацию о сотруднике неавторизованному пользователю, статус-код 401")
    public void shouldReceive401OnUnauthorizedEditEmployeeRequest() throws IOException {
        String userToken=Auth();
//        Создание компании
        int idCreatedCompany=createCompany(userToken);
//        Создание сотрудника
        HttpUrl url=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH_EMPLOYEE).build();
        Employee employeeForPost=new Employee
                (1,"Игнат",
                        "Воров",
                        "Петрович",
                        idCreatedCompany,
                        "tester@pitmail.com",
                        "http://qw2323eerty.ru",
                        "89764673826",
                        "1987-10-07",
                        true);
        RequestBody body=RequestBody.create(mapper.writeValueAsString(employeeForPost), APPLICATION_JSON);
        Request request=new Request.Builder().post(body).url(url).addHeader("x-client-token",userToken).build();
        Response response=client.newCall(request).execute();
        Employee newEmployee=mapper.readValue(response.body().string(),Employee.class);
//        Изменнение информации о сотруднике
        HttpUrl url1=HttpUrl.parse(BASE_URL).newBuilder()
                .addPathSegment(PATH_EMPLOYEE)
                .addPathSegment(String.valueOf(newEmployee.getId()))
                .build();
        Employee employeeToEdit=new Employee("Будов","testerYO@bugmail.com","http://ytrewq.com","89768765544",false);
        RequestBody body1=RequestBody.create(mapper.writeValueAsString(employeeToEdit),APPLICATION_JSON);
        Request request1=new Request.Builder().patch(body1).url(url1).build();
        Response response1=client.newCall(request1).execute();
        String body2=response1.body().string();
        assertEquals(401,response1.code());
        assertEquals(1,response1.headers("content-type").size());
        assertEquals("application/json; charset=utf-8",response1.header("content-type"));
        assertTrue(body2.startsWith("{"));
        assertTrue(body2.endsWith("}"));
    }
    @Test
    @DisplayName("Проверяем, нельзя изменить информацию о несозданном сотруднике, статус-код 404")
    public void shouldReceive404OnEditUnCreatedEmployeeRequest() throws IOException {
        String userToken=Auth();
        HttpUrl url1=HttpUrl.parse(BASE_URL).newBuilder()
                .addPathSegment(PATH_EMPLOYEE)
                .addPathSegment(Integer.toString(0))
                .build();
        Employee employeeToEdit=new Employee("Будов","testerYO@bugmail.com","http://ytrewq.com","89768765544",false);
        RequestBody body1=RequestBody.create(mapper.writeValueAsString(employeeToEdit),APPLICATION_JSON);
        Request request1=new Request.Builder().patch(body1).url(url1).addHeader("x-client-token",userToken).build();
        Response response1=client.newCall(request1).execute();
        String body2=response1.body().string();
        assertEquals(404,response1.code());
    }




    private String Auth() throws IOException {
        RequestBody bodyAuth=RequestBody.create("{\"username\":\"leonardo\",\"password\":\"leads\"}",APPLICATION_JSON);
        HttpUrl urlAuth=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH_AUTH).build();
        Request requestAuth=new Request.Builder().post(bodyAuth).url(urlAuth).build();
        Response responseAuth=client.newCall(requestAuth).execute();
        UserInfo userInfo=mapper.readValue(responseAuth.body().string(), UserInfo.class);
        return userInfo.getUserToken();
    }
    private int createCompany(String userToken) throws IOException {

        HttpUrl urlPostComp=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH_COMPANY).build();
        RequestBody bodyPostComp=RequestBody
                .create("{\"name\":\"TestCompany\",\"description\":\"Company created for test\"}",APPLICATION_JSON);
        Request requestPostComp=new Request.Builder()
                .post(bodyPostComp)
                .url(urlPostComp)
                .addHeader("x-client-token", userToken)
                .build();
        Response responsePostComp=client.newCall(requestPostComp).execute();
        Company createdCompany=mapper.readValue(responsePostComp.body().string(),Company.class);
        return createdCompany.getId();}



}
