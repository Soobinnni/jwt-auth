package com.auth.jwt.user.infrastructure.aspect;

import com.auth.jwt.user.application.exception.InvalidUserValueException;
import com.auth.jwt.user.application.exception.UserBusinessException;
import com.auth.jwt.user.application.exception.UserExceptionHandler;
import com.auth.jwt.user.domain.exception.UserException;
import com.auth.jwt.user.domain.exception.UserInvalidValueException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class UserExceptionAspect {

  @Around(
      "@within(com.auth.jwt.user.application.exception.UserExceptionHandler) && execution(public * *(..))")
  public Object convertDomainExceptionsForClass(ProceedingJoinPoint joinPoint) throws Throwable {
    UserExceptionHandler annotation =
        joinPoint.getTarget().getClass().getAnnotation(UserExceptionHandler.class);
    return handleConversion(joinPoint, annotation);
  }

  @Around("@annotation(com.auth.jwt.user.application.exception.UserExceptionHandler)")
  public Object convertDomainExceptionsForMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    UserExceptionHandler annotation =
        signature.getMethod().getAnnotation(UserExceptionHandler.class);
    return handleConversion(joinPoint, annotation);
  }

  private Object handleConversion(ProceedingJoinPoint joinPoint, UserExceptionHandler annotation)
      throws Throwable {
    String methodName = joinPoint.getSignature().getName();
    String className = joinPoint.getTarget().getClass().getSimpleName();

    if (isExcludedMethod(methodName, annotation.excludeMethods())) {
      return joinPoint.proceed();
    }

    if (!annotation.enabled()) {
      return joinPoint.proceed();
    }

    try {
      return joinPoint.proceed();
    } catch (UserInvalidValueException e) {
      log.warn(
          "{}.{} 실행 중 입력값 검증 실패 - 코드: {}, 상세: {}",
          className,
          methodName,
          e.getCode(),
          e.getMessage());
      throw new InvalidUserValueException(e.getCode(), e.getMessage());
    } catch (UserException e) {
      log.warn(
          "{}.{} 실행 중 도메인 예외 - 코드: {}, 상세: {}", className, methodName, e.getCode(), e.getMessage());
      throw new UserBusinessException(e.getCode(), e.getMessage());
    }
  }

  private boolean isExcludedMethod(String methodName, String[] excludeMethods) {
    return Arrays.asList(excludeMethods).contains(methodName);
  }
}
