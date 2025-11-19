package com.finedge.finedgeapi.audit;


import java.lang.annotation.*;


@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Audit {
    String action();
    boolean logRequest() default true;
    boolean logResponse() default false;
}
