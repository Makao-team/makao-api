package kr.co.makao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MakaoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MakaoApplication.class, args);
    }
}
