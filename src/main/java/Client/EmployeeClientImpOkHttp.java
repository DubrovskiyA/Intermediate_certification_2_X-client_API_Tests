package Client;

import Model.Business.CreateEmployee;
import Model.Business.UpdateEmployee;
import Model.Employee;
import Model.LogInterceptor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class EmployeeClientImpOkHttp implements EmployeeClient {
    String BASE_URL;
    OkHttpClient client;
    ObjectMapper mapper;
    MediaType APPLICATION_JSON=MediaType.parse("application/json; charset=utf-8");

    public EmployeeClientImpOkHttp(String url) {
        BASE_URL=url;
        client=new OkHttpClient.Builder().addNetworkInterceptor(new LogInterceptor()).build();
        mapper=new ObjectMapper();
    }
    @Override
    public List<Employee> getList(int id) throws IOException {
        HttpUrl url=HttpUrl.parse(BASE_URL).newBuilder().addQueryParameter("company",Integer.toString(id)).build();
        Request request=new Request.Builder().get().url(url).build();
        Response response=client.newCall(request).execute();
        List<Employee> list=mapper.readValue(response.body().string(), new TypeReference<List<Employee>>() {});
        return list;
    }

    @Override
    public int createEmployee(CreateEmployee employee, String userToken) throws IOException {
        RequestBody body=RequestBody.create(mapper.writeValueAsString(employee),APPLICATION_JSON);
        Request request=new Request.Builder().post(body).url(BASE_URL).addHeader("x-client-token",userToken).build();
        Response response=client.newCall(request).execute();
        Employee employee1=mapper.readValue(response.body().string(),Employee.class);
        return employee1.getId();
    }

    @Override
    public Employee getEmployeeById(int id) throws IOException {
        HttpUrl url=HttpUrl.parse(BASE_URL).newBuilder().addPathSegment(Integer.toString(id)).build();
        Request request=new Request.Builder().get().url(url).build();
        Response response=client.newCall(request).execute();
        Employee employee=mapper.readValue(response.body().string(),Employee.class);
        return employee;
    }

    @Override
    public int updateEmployee(int id, UpdateEmployee employee, String userToken) throws IOException {
        HttpUrl url=HttpUrl.parse(BASE_URL).newBuilder().addPathSegment(Integer.toString(id)).build();
        RequestBody body=RequestBody.create(mapper.writeValueAsString(employee),APPLICATION_JSON);
        Request request=new Request.Builder().patch(body).url(url).addHeader("x-client-token",userToken).build();
        Response response=client.newCall(request).execute();
        Employee employee1=mapper.readValue(response.body().string(),Employee.class);
        return employee1.getId();
    }
}
