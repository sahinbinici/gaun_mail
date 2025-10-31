package com.mobilewebservices.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Aspect
@Component  // Enabled logging aspect
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");

    @Pointcut("execution(* com.foodannouncementsnewseventservices.*.controller.*.*(..))")
    public void controllerMethods() {}

    @Pointcut("execution(* com.foodannouncementsnewseventservices.*.service.*.*(..))")
    public void serviceMethods() {}

    @Pointcut("execution(* com.foodannouncementsnewseventservices.*.repository.*.*(..))")
    public void repositoryMethods() {}

    @Around("controllerMethods()")
    public Object logAroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);

        Instant start = Instant.now();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        try {
            logger.info("Request [{}] - Starting {}.{}", requestId, className, methodName);

            // Log request parameters (be careful with sensitive data)
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                logger.debug("Request [{}] - Parameters: {}", requestId, Arrays.toString(args));
            }

            Object result = joinPoint.proceed();

            Duration duration = Duration.between(start, Instant.now());
            logger.info("Request [{}] - Completed {}.{} in {}ms",
                       requestId, className, methodName, duration.toMillis());

            // Audit log for important operations
            auditLogger.info("USER_ACTION - {} - {} - SUCCESS - Duration: {}ms",
                           className, methodName, duration.toMillis());

            return result;

        } catch (Exception ex) {
            Duration duration = Duration.between(start, Instant.now());
            logger.error("Request [{}] - Error in {}.{} after {}ms: {}",
                        requestId, className, methodName, duration.toMillis(), ex.getMessage());

            auditLogger.error("USER_ACTION - {} - {} - ERROR: {} - Duration: {}ms",
                            className, methodName, ex.getMessage(), duration.toMillis());

            throw ex;
        } finally {
            MDC.clear();
        }
    }

    @Around("serviceMethods()")
    public Object logAroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant start = Instant.now();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        try {
            logger.debug("Service call - Starting {}.{}", className, methodName);
            Object result = joinPoint.proceed();

            Duration duration = Duration.between(start, Instant.now());
            logger.debug("Service call - Completed {}.{} in {}ms",
                        className, methodName, duration.toMillis());

            return result;

        } catch (Exception ex) {
            Duration duration = Duration.between(start, Instant.now());
            logger.error("Service error - {}.{} failed after {}ms: {}",
                        className, methodName, duration.toMillis(), ex.getMessage());
            throw ex;
        }
    }

    @Around("repositoryMethods()")
    public Object logAroundRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant start = Instant.now();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        try {
            logger.debug("Database call - Starting {}.{}", className, methodName);
            Object result = joinPoint.proceed();

            Duration duration = Duration.between(start, Instant.now());
            logger.debug("Database call - Completed {}.{} in {}ms",
                        className, methodName, duration.toMillis());

            return result;

        } catch (Exception ex) {
            Duration duration = Duration.between(start, Instant.now());
            logger.error("Database error - {}.{} failed after {}ms: {}",
                        className, methodName, duration.toMillis(), ex.getMessage());
            throw ex;
        }
    }

    @AfterThrowing(pointcut = "controllerMethods() || serviceMethods() || repositoryMethods()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        logger.error("Exception in {}.{}: {}", className, methodName, ex.getMessage(), ex);

        // Log security-related exceptions to audit log
        if (ex.getMessage().contains("Authentication") || ex.getMessage().contains("Authorization")
            || ex.getMessage().contains("Access")) {
            auditLogger.warn("SECURITY_EVENT - {} - {} - Exception: {}",
                           className, methodName, ex.getMessage());
        }
    }
}
