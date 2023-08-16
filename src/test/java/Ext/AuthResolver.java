package Ext;

import Auth.AuthorizeService;
import Auth.AuthorizeServiceImpOkHttp;
import Model.LogInterceptor;
import Model.UserInfo;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.io.IOException;

public class AuthResolver implements ParameterResolver {
    private String URL="https://x-clients-be.onrender.com";
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(UserInfo.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        OkHttpClient client=new OkHttpClient().newBuilder().addNetworkInterceptor(new LogInterceptor()).build();
        AuthorizeService authorizeService=new AuthorizeServiceImpOkHttp(URL,client);
        UserInfo info=new UserInfo();
        try {
            info.setUserToken(authorizeService.auth("leonardo", "leads"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return info;
    }
}
