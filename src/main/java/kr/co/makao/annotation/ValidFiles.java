package kr.co.makao.annotation;

import jakarta.validation.Constraint;
import kr.co.makao.validation.FilesValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FilesValidator.class)
@Target({ElementType.TYPE_USE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFiles {
    String message() default "File size is too large";

    /**
     * Maximum size of the file in bytes
     *
     * @example 1024 * 1024 * 30 // 30MB
     */
    long maxSize(); // 30MB
}
