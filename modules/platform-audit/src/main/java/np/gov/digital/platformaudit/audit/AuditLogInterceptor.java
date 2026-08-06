package np.gov.digital.platformaudit.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogInterceptor {

    private final AuditLogService auditLogService;
    @Around("@annotation(np.gov.digital.platformaudit.audit.Auditable)")
    public Object intercept(ProceedingJoinPoint joinPoint) throws Throwable {

        // Read the @Auditable annotation from the method
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Auditable auditable = method.getAnnotation(Auditable.class);

        AuditEventType eventType  = auditable.value();
        String         annotation = auditable.description();
        String         className  = joinPoint.getTarget().getClass().getSimpleName();
        String         methodName = method.getName();

        // Try to extract a UUID from method arguments to use as entityId
        UUID entityId = extractEntityId(joinPoint.getArgs());

        // Build description — use annotation value if provided, else method name
        String description = annotation.isBlank()
                ? className + "." + methodName + "()"
                : annotation;

        try {
            // Run the actual method
            Object result = joinPoint.proceed();

            // Method succeeded — write audit log
            auditLogService.log(eventType, entityId, description);

            return result;

        } catch (Throwable ex) {
            // Method failed — still write audit log but mark as failed
            auditLogService.log(
                    eventType,
                    entityId,
                    description + " — FAILED: " + ex.getMessage()
            );
            // Re-throw so the caller gets the exception normally
            throw ex;
        }
    }
    private UUID extractEntityId(Object[] args) {
        if (args == null) return null;
        for (Object arg : args) {
            if (arg instanceof UUID) {
                return (UUID) arg;
            }
        }
        return null;
    }
}