package kr.co.makao.integration;

import jakarta.persistence.EntityManager;
import kr.co.makao.integration.config.PostgreInitializer;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.support.TransactionTemplate;

@ActiveProfiles({"test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(initializers = PostgreInitializer.class)
public class IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private EntityManager em;

    @Autowired
    private TransactionTemplate template;
}
  
