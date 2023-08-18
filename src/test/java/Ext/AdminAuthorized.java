package Ext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AdminAuthorized {
    String username() default "";

    String password() default "";
}
