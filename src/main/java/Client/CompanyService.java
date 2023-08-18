package Client;

import Auth.Authorizable;

import java.io.IOException;

public interface CompanyService extends Authorizable {
    int createCompany() throws IOException;
}
