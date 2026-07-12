package np.gov.digitalnepal.platformaudit.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Import:
 *   import np.gov.digitalnepal.platformaudit.audit.Auditable;
 *   import np.gov.digitalnepal.platformaudit.audit.AuditEventType;
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    AuditEventType value();
    String description() default "";
}