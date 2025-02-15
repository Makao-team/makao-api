package kr.co.makao.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("kr.co.makao.entity")
@EnableJpaRepositories("kr.co.makao.repository")
@EnableJpaAuditing
public @interface EnableCommonJpa {
}
