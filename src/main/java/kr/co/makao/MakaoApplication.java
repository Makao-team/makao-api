package kr.co.makao;

import kr.co.makao.config.EnableCommonJpa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@EnableCommonJpa
@RequiredArgsConstructor
@SpringBootApplication
@Slf4j
public class MakaoApplication {
    private final Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(MakaoApplication.class, args);
    }

    @EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    public void logApplicationPort() {
        String port = environment.getProperty("local.server.port");
        log.debug("=============== MakaoApplication started on [http://localhost:{}] ===============", port);
    }
}
