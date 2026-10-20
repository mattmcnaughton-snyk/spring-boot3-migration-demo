package com.example.inventory.audit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Logs every service call. Depends on spring-boot-starter-aop, which Spring Boot 4
 * renames to spring-boot-starter-aspectj.
 */
@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    @AfterReturning("execution(public * com.example.inventory.service..*(..))")
    public void audit(JoinPoint joinPoint) {
        // VULNERABLE: log injection, raw user-controlled arguments written to the log.
        log.info("AUDIT {} args={}", joinPoint.getSignature().toShortString(),
                Arrays.toString(joinPoint.getArgs()));
    }
}
