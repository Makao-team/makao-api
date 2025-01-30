package kr.co.makao.integration;

import kr.co.makao.EnableCommonJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

@Import(TestJpaConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest(
    properties = {
        "logging.level.org.springframework.jdbc.datasource.init=DEBUG",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=always",
    }
)
public abstract class RepositoryTest {

    @Autowired
    protected TestEntityManager em;

    @Autowired
    protected TransactionTemplate template;

    protected <T> T persist(T entity) {
        em.persist(entity);
        em.flush();
        em.clear();
        return entity;
    }
}

@SpringBootApplication
class TestApplication {
}

@EnableTransactionManagement
@TestConfiguration
@EnableCommonJpa
class TestJpaConfig {
}
