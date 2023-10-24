package com.craft.authentication.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)"+
            " || within(@org.springframework.context.annotation.Configuration *)" )
    public void springBeanPointCut(){
    }

    @Pointcut("within(com.craft.authentication..*)"+
            " || within(com.craft.authentication.service..*)"+
            " || within(com.craft.authentication.controller..*)"+
            " || within(com.craft.authentication.config..*)"+
            " || within(com.craft.authentication.repository..*)")
    public void applicationPackagePointCut(){
    }

    @AfterThrowing(pointcut = "applicationPackagePointCut() && springBeanPointCut()",throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint,Throwable e){
        logger.error("Exception in {}.{}() with cause = {}",joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),e.getCause() != null ? e.getCause() : "NULL");
    }

    @Around("applicationPackagePointCut() && springBeanPointCut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (logger.isDebugEnabled()) {
            logger.debug("Enter: {}.{}() with argument[s] = {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        }
        try {
            Object result = joinPoint.proceed();
            if (logger.isDebugEnabled()) {
                logger.debug("Exit: {}.{}() with result = {}", joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(), result);
            }
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            throw e;
        }
    }
}
