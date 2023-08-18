package Client;

import Model.Company;
import Model.Business.CreateCompanyRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;

public class CompanyServiceImpOkHttp implements CompanyService{
    private String BASE_URL;
    private String PATH="company";
    private OkHttpClient client;
    private ObjectMapper mapper;
    private MediaType APPLICATION_JSON=MediaType.parse("application/json; charset=utf-8");
    private static final String NAME="TEST";
    private static final String DESCRIPTION="FOR TEST";

    private String userToken;

    public CompanyServiceImpOkHttp(OkHttpClient client, String BASE_URL) {
        this.BASE_URL = BASE_URL;
        this.client = client;
        mapper=new ObjectMapper();
    }

    @Override
    public int createCompany() throws IOException {
        HttpUrl urlPostComp=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH).build();
        RequestBody bodyPostComp=RequestBody
                .create(mapper.writeValueAsString(new CreateCompanyRequest(NAME,DESCRIPTION)),APPLICATION_JSON);
        Request requestPostComp=new Request.Builder()
                .post(bodyPostComp)
                .url(urlPostComp)
                .addHeader("x-client-token", userToken)
                .build();
        Response responsePostComp=client.newCall(requestPostComp).execute();
        Company createdCompany=mapper.readValue(responsePostComp.body().string(), Company.class);
        return createdCompany.getId();
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
