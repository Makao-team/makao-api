package kr.co.makao;

import jakarta.persistence.EntityManager;
import kr.co.makao.config.PostgreInitializer;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = PostgreInitializer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private EntityManager em;

    @Autowired
    private TransactionTemplate template;
}

