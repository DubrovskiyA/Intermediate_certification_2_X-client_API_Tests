package Auth;

import Model.UserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;

public class AuthorizeServiceImpOkHttp implements AuthorizeService{
    String BASE_URL;
    String PATH="auth/login";
    OkHttpClient client;
    ObjectMapper mapper;
    MediaType APPLICATION_JSON=MediaType.parse("application/json; charset=utf-8");

    public AuthorizeServiceImpOkHttp(String url, OkHttpClient client) {
        this.BASE_URL = url;
        this.client = client;
        this.mapper = new ObjectMapper();
    }

    @Override
    public String auth(String username, String password) throws IOException {
        RequestBody bodyAuth=RequestBody.create("{\"username\":\""+username+"\",\"password\":\""+password+"\"}",APPLICATION_JSON);
        HttpUrl urlAuth=HttpUrl.parse(BASE_URL).newBuilder().addPathSegments(PATH).build();
        Request request=new Request.Builder().post(bodyAuth).url(urlAuth).build();
        Response response=client.newCall(request).execute();
        UserInfo userInfo=mapper.readValue(response.body().string(), UserInfo.class);
        return userInfo.getUserToken();
    }
}
