package kr.co.makao.config;

import io.minio.MinioClient;
import kr.co.makao.client.bucket.BucketClientImpl;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.Closeable;

public class MinioInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Closeable {
    private static final Logger logger = LoggerFactory.getLogger(MinioInitializer.class);
    private static final String MINIO_IMAGE = "minio/minio:latest";
    private static final String BUCKET_NAME = "test-bucket";
    private static final String ACCESS_KEY = "testAccessKey";
    private static final String SECRET_KEY = "testSecretKey";

    private static GenericContainer<?> container;
    private static MinioClient minio;
    private static BucketClientImpl bucketClient;

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
        logger.info("Initializing Minio test container");

        if (container == null) {
            container = new GenericContainer<>(MINIO_IMAGE)
                    .withEnv("MINIO_ACCESS_KEY", ACCESS_KEY)
                    .withEnv("MINIO_SECRET_KEY", SECRET_KEY)
                    .withCommand("server /data")
                    .withExposedPorts(9000)
                    .waitingFor(Wait.forHttp("/minio/health/ready").forStatusCode(200));
            container.start();

            minio = MinioClient.builder()
                    .endpoint(String.format("http://%s:%d", container.getHost(), container.getMappedPort(9000)))
                    .credentials(ACCESS_KEY, SECRET_KEY)
                    .build();

        }

        if (bucketClient == null) {
            bucketClient = new BucketClientImpl(minio, BUCKET_NAME);
            bucketClient.create();
        }

        TestPropertyValues.of(
                "minio.endpoint=" + String.format("http://%s:%d", container.getHost(), container.getMappedPort(9000)),
                "minio.access-key=" + ACCESS_KEY,
                "minio.secret-key=" + SECRET_KEY,
                "minio.bucket-name=" + BUCKET_NAME,
                "minio.url-expiration-hours=1"
        ).applyTo(applicationContext.getEnvironment());
    }

    @Override
    public void close() {
        if (bucketClient != null) {
            bucketClient.delete();
            bucketClient = null;
        }

        if (container != null) {
            container.close();
            container = null;
        }
    }
}
