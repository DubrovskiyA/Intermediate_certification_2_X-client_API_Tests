package Auth;

import Model.UserInfo;

import java.io.IOException;

public interface AuthorizeService {
    String auth() throws IOException;
}
