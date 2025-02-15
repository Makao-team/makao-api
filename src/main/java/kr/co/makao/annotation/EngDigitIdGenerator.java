package kr.co.makao.annotation;


import kr.co.makao.config.RandomEngDigitIdGenerator;
import org.hibernate.annotations.IdGeneratorType;
import org.hibernate.annotations.ValueGenerationType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@IdGeneratorType(RandomEngDigitIdGenerator.class)
@ValueGenerationType(generatedBy = RandomEngDigitIdGenerator.class)
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface EngDigitIdGenerator {
    int length();
}

