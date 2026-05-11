package com.FurryArtyPawPrints.ViewModule.errorsHandling;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Component
public class GlobalExceptionAspect {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionAspect.class);

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object handleControllerExceptions(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return pjp.proceed();
        } catch (Exception ex) {
            logger.error("Internal server error in method: {} - {}", pjp.getSignature(), ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "errorCode", "INTERNAL_SERVER_ERROR",
                            "message", "An unexpected error occurred. Please contact support."
                    ));
        }
    }
}

