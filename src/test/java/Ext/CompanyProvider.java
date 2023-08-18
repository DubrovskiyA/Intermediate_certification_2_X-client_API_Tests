package Ext;

import Auth.AuthorizeService;
import Auth.AuthorizeServiceImpOkHttp;
import Client.*;
import Model.LogInterceptor;
import Model.UserInfo;
import Ext.props.PropertyProvider;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.io.IOException;

public class CompanyProvider implements ParameterResolver {
    private final static String URL = PropertyProvider.getInstance().getProps().getProperty("test.url");
    private final static String USER = PropertyProvider.getInstance().getProps().getProperty("test.admin.user");
    private final static String PASS = PropertyProvider.getInstance().getProps().getProperty("test.admin.pass");

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(CompanyService.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        OkHttpClient client= new OkHttpClient.Builder().addNetworkInterceptor(new LogInterceptor()).build();
        AuthorizeService authorizeService=new AuthorizeServiceImpOkHttp(client,URL,USER,PASS);
        String s;
        try {
           s=authorizeService.auth();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CompanyService companyService=new CompanyServiceImpOkHttp(client,URL);
        companyService.setUserToken(s);

        return companyService;
    }
}
