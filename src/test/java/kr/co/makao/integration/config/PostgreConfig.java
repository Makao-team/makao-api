package kr.co.makao.integration.config;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Closeable;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Profiles;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgreConfig implements ApplicationContextInitializer<ConfigurableApplicationContext>, Closeable {

    private static final Logger logger = LoggerFactory.getLogger(PostgreConfig.class);
    private static final String POSTGRES_IMAGE = "postgres:15";

    private static PostgreSQLContainer<?> container;

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
        // gradle 커맨드 앞에서 설정 가능 ex) APP_INTEGRATION_TEST_ADDITIONAL_PROFILE=postgre ./gradlew test
        String envProfile = System.getenv("APP_INTEGRATION_TEST_ADDITIONAL_PROFILE");
        boolean isPostgreProfile = "postgre".equals(envProfile) || applicationContext.getEnvironment().acceptsProfiles(Profiles.of("postgre"));

        if (isPostgreProfile) {
            logger.info("Initializing Postgres test container");

            if (container == null) {
                container = new PostgreSQLContainer<>(POSTGRES_IMAGE)
                        .withDatabaseName("makao-test")
                        .withUsername("test")
                        .withPassword("test")
                        .withUrlParam("stringtype", "unspecified");
                container.start();
            }

            TestPropertyValues.of(
                    "eco.datasource.read.hikari.jdbc-url=" + container.getJdbcUrl(),
                    "eco.datasource.read.hikari.username=" + container.getUsername(),
                    "eco.datasource.read.hikari.password=" + container.getPassword(),
                    "eco.datasource.write.hikari.jdbc-url=" + container.getJdbcUrl(),
                    "eco.datasource.write.hikari.username=" + container.getUsername(),
                    "eco.datasource.write.hikari.password=" + container.getPassword(),
                    "spring.jpa.properties.hibernate.dialects=org.hibernate.dialect.PostgreSQLDialect"
            ).applyTo(applicationContext.getEnvironment());
        }
    }

    @Override
    public void close() {
        if (container != null) {
            container.close();
            container = null;
        }
    }
}
