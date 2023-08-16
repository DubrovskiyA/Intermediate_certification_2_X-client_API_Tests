package Model.Business;

import Model.Employee;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

public class EmployeeClientImpOkHttp implements EmployeeClient {
    OkHttpClient client;
    String BASE_URL="https://x-clients-be.onrender.com/employee";
    ObjectMapper mapper;

    public EmployeeClientImpOkHttp(String url) {
        BASE_URL=url;
        client=new OkHttpClient.Builder().build();
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
    public int createEmployee(Employee employee) {
        HttpUrl url;
        return 0;
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
    public Employee editEmployee(int id) {


        return null;
    }
}
