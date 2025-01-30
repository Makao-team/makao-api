package kr.co.makao;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackageClasses = EnableCommonJpa.class)
@EnableJpaRepositories(basePackageClasses = EnableCommonJpa.class)
@EnableJpaAuditing
public @interface EnableCommonJpa {
}
