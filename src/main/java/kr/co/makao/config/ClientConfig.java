package kr.co.makao.config;

import io.minio.MinioClient;
import kr.co.makao.client.ImageClient;
import kr.co.makao.client.ImageClientImpl;
import kr.co.makao.client.NoOpImageClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(ClientConfig.class);

    /**
     * MinIO 클라이언트 빈 등록
     * - 설정값이 없을 경우 기본 테스트용 값을 가진 MinIO 클라이언트를 반환하여 NPE 방지
     */
    @Bean
    @ConditionalOnMissingBean(MinioClient.class)
    public MinioClient minioClient(
            @Value("${minio.endpoint:}") String endpoint,
            @Value("${minio.access-key:}") String accessKey,
            @Value("${minio.secret-key:}") String secretKey
    ) {
        if (endpoint.isBlank() || accessKey.isBlank() || secretKey.isBlank()) {
            logger.warn("MinIO 설정이 없으므로 기본 테스트용 MinIO 클라이언트를 생성합니다.");
            return MinioClient.builder()
                    .endpoint("http://localhost:9000")
                    .credentials("test-access-key", "test-secret-key")
                    .build();
        }
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(ImageClient.class)
    public ImageClient imageClient(
            MinioClient minioClient,
            @Value("${minio.bucket-name:}") String bucketName,
            @Value("${minio.url-expiration-hours:0}") int urlExpirationHours
    ) {
        if (bucketName.isBlank() || urlExpirationHours <= 0) {
            logger.warn("MinIO 설정값이 없으므로 NoOpImageClientImpl을 사용합니다.");
            return new NoOpImageClientImpl();
        }
        return new ImageClientImpl(minioClient);
    }
}
