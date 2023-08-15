package Model.Business;

import Model.Contract.Employee;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

public class EmployeeServiceImpOkHttp implements EmployeeService{
    OkHttpClient client;
    String BASE_URL="https://x-clients-be.onrender.com";
    String PATH="employee";
    ObjectMapper mapper;

    public EmployeeServiceImpOkHttp() {
        client.newBuilder().build();
        mapper=new ObjectMapper();
    }

    @Override
    public List<Employee> getList(int id) throws IOException {
        HttpUrl url=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH).addQueryParameter("company",Integer.toString(id)).build();
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
    public Employee getEmployeeById(int id) {

        return null;
    }

    @Override
    public Employee editEmployee(int id) {
        return null;
    }
}
