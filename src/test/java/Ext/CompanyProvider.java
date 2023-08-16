package Ext;

import Auth.AuthorizeService;
import Auth.AuthorizeServiceImpOkHttp;
import Model.Company;
import Model.LogInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.io.IOException;

public class CompanyProvider implements ParameterResolver {
    private String BASE_URL="https://x-clients-be.onrender.com";
    private String PATH="company";
    MediaType APPLICATION_JSON=MediaType.parse("application/json; charset=utf-8");
    ObjectMapper mapper=new ObjectMapper();

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(Company.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        OkHttpClient client=new OkHttpClient.Builder().addNetworkInterceptor(new LogInterceptor()).build();
        AuthorizeService authorizeService=new AuthorizeServiceImpOkHttp(BASE_URL,client);
        String userToken= null;
        try {
            userToken = authorizeService.auth("leonardo","leads");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HttpUrl urlPostComp=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH).build();
        RequestBody bodyPostComp=RequestBody
                .create("{\"name\":\"TestCompany\",\"description\":\"Company created for test\"}",APPLICATION_JSON);
        Request requestPostComp=new Request.Builder()
                .post(bodyPostComp)
                .url(urlPostComp)
                .addHeader("x-client-token", userToken)
                .build();
        Response responsePostComp= null;
        try {
            responsePostComp = client.newCall(requestPostComp).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Company createdCompany= null;
        try {
            createdCompany = mapper.readValue(responsePostComp.body().string(), Company.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return createdCompany;
    }
}
