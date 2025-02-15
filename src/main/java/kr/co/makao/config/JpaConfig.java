package kr.co.makao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "kr.co.makao.repository")
@EnableJpaAuditing
@Configuration
public class JpaConfig {
}
