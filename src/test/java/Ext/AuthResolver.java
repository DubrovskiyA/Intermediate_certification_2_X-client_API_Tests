package Ext;

import Auth.AuthorizeService;
import Auth.AuthorizeServiceImpOkHttp;
import Ext.props.PropertyProvider;
import Model.LogInterceptor;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class AuthResolver implements ParameterResolver {
    private final static String URL = PropertyProvider.getInstance().getProps().getProperty("test.url");
    private final static String ADMIN_USER = PropertyProvider.getInstance().getProps().getProperty("test.admin.user");
    private final static String ADMIN_PASS = PropertyProvider.getInstance().getProps().getProperty("test.admin.pass");
    private final static String CLIENT_USER = PropertyProvider.getInstance().getProps().getProperty("test.client.user");
    private final static String CLIENT_PASS = PropertyProvider.getInstance().getProps().getProperty("test.client.pass");

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(AuthorizeService.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        OkHttpClient client=new OkHttpClient().newBuilder().addNetworkInterceptor(new LogInterceptor()).build();
        AuthorizeService authorizeService = null;
        if (parameterContext.isAnnotated(AdminAuthorized.class)){
            authorizeService=new AuthorizeServiceImpOkHttp(client,URL, ADMIN_USER, ADMIN_PASS);
        }
        if (parameterContext.isAnnotated(ClientAuthorized.class)){
            authorizeService=new AuthorizeServiceImpOkHttp(client,URL, CLIENT_USER, CLIENT_PASS);
        }
        return authorizeService;
    }
}
