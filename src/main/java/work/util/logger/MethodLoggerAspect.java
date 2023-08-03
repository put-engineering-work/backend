package work.util.logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class MethodLoggerAspect {

    private final Logger logger = LoggerFactory.getLogger(MethodLoggerAspect.class);

    @Value("${rest.methods.logger.enabled:true}")
    private boolean restMethodsLoggerEnabled;

    @Value("${service.methods.logger.enabled:true}")
    private boolean serviceMethodsLoggerEnabled;

    @Around("@within(restController)")
    public Object logRestControllerMethod(ProceedingJoinPoint joinPoint, RestController restController) throws Throwable {
        return loggerFormatter(joinPoint, restMethodsLoggerEnabled);
    }

    @Around("@within(service)")
    public Object logServiceMethod(ProceedingJoinPoint joinPoint, Service service) throws Throwable {
        return loggerFormatter(joinPoint, serviceMethodsLoggerEnabled);
    }

    private Object loggerFormatter(ProceedingJoinPoint joinPoint, boolean serviceMethodsLoggerEnabled) throws Throwable {
        if (serviceMethodsLoggerEnabled) {
            String controllerName = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();
            Object[] args = joinPoint.getArgs();

            long startTime = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            logger.info("{} ==> {}() - start: request = {}", controllerName, methodName, args);
            logger.info("{} ==> {}() - end: response = {} (Execution Time: {} ms)",
                    controllerName, methodName, Objects.toString(result, "null"), executionTime);

            return result;
        } else {
            return joinPoint.proceed();
        }
    }
}
