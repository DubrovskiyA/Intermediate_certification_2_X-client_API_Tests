package Test;

import Model.LogInterceptor;
import Model.Company;
import Model.Contract.CreateEmployeeWithOnlyReqFields;
import Model.Employee;
import Model.UserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE,-999,-1,0,1,999,Integer.MAX_VALUE})
    @DisplayName("Получение списка сотрудников компании, статус-код 200, заголовок content-type")
    public void shouldReceive200OnGetRequest(int companyId) throws IOException {
        HttpUrl url=HttpUrl.parse(BASE_URL)
                .newBuilder()
                .addPathSegments(PATH_EMPLOYEE)
                .addQueryParameter("company",Integer.toString(companyId))
                .build();
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
    @DisplayName("Создание сотрудника только с обязательными полями в теле запроса, статус-код 201")
    public void shouldReceive201OnCreateEmployeeWithRequiredFields() throws IOException {
        String userToken=Auth();
//        Создание компании
        int idCreatedCompany = createCompany(userToken);
//        Создание сотрудника
        HttpUrl url=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH_EMPLOYEE).build();
        CreateEmployeeWithOnlyReqFields employeeToPost=
                new CreateEmployeeWithOnlyReqFields(1,"Bill","Ronald",idCreatedCompany,false);
        RequestBody body=RequestBody.create(mapper.writeValueAsString(employeeToPost), APPLICATION_JSON);
        Request request=new Request.Builder().post(body).url(url).addHeader("x-client-token",userToken).build();
        Response response=client.newCall(request).execute();
        String bodyResp=response.body().string();
        assertEquals(201,response.code());
        assertEquals(1,response.headers("content-type").size());
        assertEquals("application/json; charset=utf-8",response.header("content-type"));
        assertTrue(bodyResp.startsWith("{"));
        assertTrue(bodyResp.endsWith("}"));
        assertTrue(bodyResp.contains("\"id\":"));
    }
    @Test
    @DisplayName("Создание сотрудника со всеми полями в теле запроса, статус-код 201")
    public void shouldReceive201OnCreateEmployeeWithAllFieldsRequest() throws IOException {
        String userToken=Auth();
//        Создание компании
        int idCreatedCompany=createCompany(userToken);
//        Создание сотрудника
        HttpUrl url=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH_EMPLOYEE).build();
        RequestBody body=RequestBody.create(mapper.writeValueAsString(new Employee
                (1,"Fill",
                        "Bugs",
                        "Edward",
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
        assertTrue(bodyResp.contains("\"id\":"));
    }
    @Test
    @DisplayName("Создание сотрудника неавторизованным пользователем без заголовка x-client-token, статус-код 401")
    public void shouldReceive401OnUnauthorizedCreateEmployeeWithAllFieldsRequest() throws IOException {
        String userToken=Auth();
//        Создание компании
        int idCreatedCompany=createCompany(userToken);
//        Создание сотрудника
        HttpUrl url=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH_EMPLOYEE).build();
        RequestBody body=RequestBody.create(mapper.writeValueAsString(new Employee
                (1,"Fill",
                        "Bugs",
                        "Edward",
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
    @ParameterizedTest
    @DisplayName("Создание сотрудника неавторизованным пользователем с заголовком x-client-token, статус-код 401")
    @ValueSource(strings = {""," ","-9999999","-1","0","1","10000000","wqeyrqwerqwteq","awdaw637723#&!iisd87667"})
    public void shouldReceive401OnUnauthorizedCreateEmployeeRequestWithFakeToken(String fakeUserToken) throws IOException{
        String userToken=Auth();
//        Создание компании
        int idCreatedCompany=createCompany(userToken);
//        Создание сотрудника
        HttpUrl url=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH_EMPLOYEE).build();
        RequestBody body=RequestBody.create(mapper.writeValueAsString(new Employee
                (1,"Fill",
                        "Bugs",
                        "Edward",
                        idCreatedCompany,
                        "test@pitmail.com",
                        "http://qweerty.ru",
                        "89764563826",
                        "1994-04-01",
                        true)), APPLICATION_JSON);
        Request request=new Request.Builder().post(body).url(url).addHeader("x-client-token",fakeUserToken).build();
        Response response=client.newCall(request).execute();
        String bodyResp=response.body().string();
        assertEquals(401,response.code());
        assertEquals(1,response.headers("content-type").size());
        assertEquals("application/json; charset=utf-8",response.header("content-type"));
        assertTrue(bodyResp.startsWith("{"));
        assertTrue(bodyResp.endsWith("}"));
    }
    @Test
    @DisplayName("Получение сотрудника по его id, статус-код 200, заголовок content-type")
    public void shouldReceive200OnGetEmployeeRequest() throws IOException {
        String userToken=Auth();
//        Создание компании
        int idCreatedCompany=createCompany(userToken);
//        Создание сотрудника
        HttpUrl url=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH_EMPLOYEE).build();
        Employee employeeForPost=new Employee
                (1,"Fill",
                        "Bugs",
                        "Edward",
                        idCreatedCompany,
                        "test@pitmail.com",
                        "http://qweerty.ru",
                        "89764563826",
                        "1994-04-01",
                        true);
        RequestBody body=RequestBody.create(mapper.writeValueAsString(employeeForPost), APPLICATION_JSON);
        Request request=new Request.Builder().post(body).url(url).addHeader("x-client-token",userToken).build();
        Response response=client.newCall(request).execute();
        Employee newEmployee=mapper.readValue(response.body().string(),Employee.class);
//        Запрос сотрудника по id
        HttpUrl url1=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH_EMPLOYEE).addPathSegments(String.valueOf(newEmployee.getId())).build();
        Request request1=new Request.Builder().get().url(url1).build();
        Response response1=client.newCall(request1).execute();
        String bodyString=response1.body().string();
        Employee EmployeePostedCall=mapper.readValue(bodyString,Employee.class);
        List<String> listOfFields= Arrays.stream(bodyString.split(",")).toList();

        assertEquals(200,response1.code());
        assertEquals(1,response.headers("content-type").size());
        assertEquals("application/json; charset=utf-8",response.header("content-type"));
        assertEquals(employeeForPost.getFirstName(),EmployeePostedCall.getFirstName());
        assertEquals(employeeForPost.getLastName(),EmployeePostedCall.getLastName());
        assertEquals(employeeForPost.getMiddleName(),EmployeePostedCall.getMiddleName());
        assertEquals(employeeForPost.getCompanyId(),EmployeePostedCall.getCompanyId());
        assertEquals(employeeForPost.getEmail(),EmployeePostedCall.getEmail());
        assertEquals(employeeForPost.getUrl(),EmployeePostedCall.getUrl());
        assertEquals(employeeForPost.getPhone(),EmployeePostedCall.getPhone());
        assertEquals(employeeForPost.getBirthdate(),EmployeePostedCall.getBirthdate());
        assertEquals(employeeForPost.getIsActive(),EmployeePostedCall.getIsActive());
        assertEquals(10,listOfFields.size());
    }
    @Test
    @DisplayName("Получение несозданного сотрудника по id, статус-код 404")
    public void shouldReceive404OnGetUncreatedEmployeeRequest() throws IOException {
        HttpUrl url=HttpUrl.parse(BASE_URL)
                .newBuilder()
                .addPathSegments(PATH_EMPLOYEE)
                .addPathSegments(Integer.toString(0))
                .build();
        Request request=new Request.Builder().get().url(url).build();
        Response response=client.newCall(request).execute();
        assertEquals(404,response.code());
    }
    @Test
    @DisplayName("Изменение информации о сотруднике, статус-код 201")
    public void shouldReceive201OnEditEmployeeRequest() throws IOException {
        String userToken=Auth();
//        Создание компании
        int idCreatedCompany=createCompany(userToken);
//        Создание сотрудника
        HttpUrl url=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH_EMPLOYEE).build();
        Employee employeeForPost=new Employee
                (1,"Fill",
                        "Bugs",
                        "Edward",
                        idCreatedCompany,
                        "test@pitmail.com",
                        "http://qweerty.ru",
                        "89764563826",
                        "1994-04-01",
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
        Employee employeeToEdit=
                new Employee("Novak","testerYO@bugmail.com","http://ytrewq.com","89768765544",false);
        RequestBody body1=RequestBody.create(mapper.writeValueAsString(employeeToEdit),APPLICATION_JSON);
        Request request1=new Request.Builder().patch(body1).url(url1).addHeader("x-client-token",userToken).build();
        Response response1=client.newCall(request1).execute();
        String bodyString=response1.body().string();
        Employee employeeEdited=mapper.readValue(bodyString,Employee.class);
        List<String> listOfFields= Arrays.stream(bodyString.split(",")).toList();

        assertEquals(201,response1.code());
        assertEquals(employeeToEdit.getLastName(),employeeEdited.getLastName());
        assertEquals(employeeToEdit.getEmail(),employeeEdited.getEmail());
        assertEquals(employeeToEdit.getUrl(),employeeEdited.getUrl());
        assertEquals(employeeToEdit.getPhone(),employeeEdited.getPhone());
        assertEquals(employeeToEdit.getIsActive(),employeeEdited.getIsActive());
        assertEquals(10,listOfFields.size());

    }
    @Test
    @DisplayName("Изменение информации о сотруднике неавторизованным пользователем без заголовка x-client-token, статус-код 401")
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
        assertEquals("{\"statusCode\":401,\"message\":\"Unauthorized\"}",body2);
    }
    @ParameterizedTest
    @DisplayName("Изменение информации о сотруднике неавторизованным пользователем с заголовком x-client-token, статус-код 401")
    @ValueSource(strings = {""," ","-9999999","-1","0","1","10000000","wqeyrqwerqwteq","awdaw637723#&!iisd87667"})
    public void shouldReceive401OnUnauthorizedEditEmployeeRequestWithFakeToken(String fakeUserToken) throws IOException {
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
        Request request1=new Request.Builder().patch(body1).url(url1).addHeader("x-client-token",fakeUserToken).build();
        Response response1=client.newCall(request1).execute();
        String body2=response1.body().string();
        assertEquals(401,response1.code());
        assertEquals(1,response1.headers("content-type").size());
        assertEquals("application/json; charset=utf-8",response1.header("content-type"));
        assertEquals("{\"statusCode\":401,\"message\":\"Unauthorized\"}",body2);
    }
    @Test
    @DisplayName("Изменение информации о несозданном сотруднике, статус-код 404")
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
