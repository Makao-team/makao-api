package kr.co.makao.config;

import io.minio.MinioClient;
import kr.co.makao.client.bucket.BucketClient;
import kr.co.makao.client.bucket.BucketClientImpl;
import kr.co.makao.client.bucket.NoOpBucketClientImpl;
import kr.co.makao.client.image.ImageClient;
import kr.co.makao.client.image.ImageClientImpl;
import kr.co.makao.client.image.NoOpImageClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {
    private static final Logger logger = LoggerFactory.getLogger(ClientConfig.class);

    @Bean
    public MinioClient minioClient(
            @Value("${minio.endpoint:}") String endpoint,
            @Value("${minio.access-key:}") String accessKey,
            @Value("${minio.secret-key:}") String secretKey
    ) {
        if (endpoint.isBlank() || accessKey.isBlank() || secretKey.isBlank()) {
            // [Todo] 자체 로그 붙이기
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
    public ImageClient imageClient(
            MinioClient minioClient,
            @Value("${minio.bucket-name:}") String bucketName,
            @Value("${minio.url-expiration-hours:0}") int urlExpirationHours
    ) {
        if (bucketName.isBlank() || urlExpirationHours <= 0) {
            logger.warn("MinIO 설정값이 없으므로 NoOpImageClientImpl을 사용합니다.");
            return new NoOpImageClientImpl();
        }
        return new ImageClientImpl(minioClient, bucketName, urlExpirationHours);
    }

    @Bean
    public BucketClient bucketClient(
            MinioClient minioClient,
            @Value("${minio.bucket-name:}") String bucketName
    ) {
        if (bucketName.isBlank()) {
            logger.warn("MinIO 설정값이 없으므로 NoOpBucketClientImpl을 사용합니다.");
            return new NoOpBucketClientImpl();
        }
        return new BucketClientImpl(minioClient, bucketName);
    }

    @Bean
    public ApplicationRunner initializeMinioBucket(
            BucketClient bucketClient
    ) {
        return args -> {
            if (!bucketClient.exists()) {
                logger.info("MinIO 버킷이 존재하지 않아 생성합니다.");
                bucketClient.create();
            }
        };
    }
}
