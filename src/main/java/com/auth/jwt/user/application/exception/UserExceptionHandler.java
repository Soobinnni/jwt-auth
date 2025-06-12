package com.auth.jwt.user.application.exception;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserExceptionHandler {
  boolean enabled() default true;

  String[] excludeMethods() default {};
}
