package kr.co.makao.integration.client;

import kr.co.makao.MakaoApplication;
import kr.co.makao.client.ImageClientImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = MakaoApplication.class)
@EnableAutoConfiguration(exclude = {HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class})
class ImageClientImplTest {
    private static final GenericContainer<?> MINIO_CONTAINER =
            new GenericContainer<>(DockerImageName.parse("minio/minio:latest"))
                    .withEnv("MINIO_ACCESS_KEY", "testAccessKey")
                    .withEnv("MINIO_SECRET_KEY", "testSecretKey")
                    .withCommand("server /data")
                    .withExposedPorts(9000)
                    .waitingFor(Wait.forHttp("/minio/health/ready").forStatusCode(200));
    private static final String bucketName = "test-bucket";
    @Autowired
    private ImageClientImpl imageClientImpl;

    @BeforeAll
    static void setUp() {
        MINIO_CONTAINER.start();
    }

    @AfterAll
    static void tearDown() {
        MINIO_CONTAINER.stop();
    }

    @DynamicPropertySource
    static void minIOProperties(DynamicPropertyRegistry registry) {
        String endpoint = String.format("http://%s:%d", MINIO_CONTAINER.getHost(), MINIO_CONTAINER.getMappedPort(9000));
        registry.add("minio.endpoint", () -> endpoint);
        registry.add("minio.access-key", () -> "testAccessKey");
        registry.add("minio.secret-key", () -> "testSecretKey");
        registry.add("minio.bucket-name", () -> bucketName);
        registry.add("minio.url-expiration-hours", () -> 1);
    }

    @BeforeEach
    void createBucket() {
        imageClientImpl.createBucket(bucketName);
    }

    @AfterEach
    void deleteAll() {
        imageClientImpl.deleteAll();
        imageClientImpl.deleteBucket(bucketName);
    }

    @Nested
    class createBucket {
        @Test
        void createBucket_성공() {
            imageClientImpl.createBucket("test-bucket-2");

            assertTrue(imageClientImpl.existsBucket("test-bucket-2"));
        }
    }

    @Nested
    class deleteBucket {
        @Test
        void deleteBucket_성공() {
            imageClientImpl.createBucket("test-bucket-2");
            imageClientImpl.deleteBucket("test-bucket-2");

            assertFalse(imageClientImpl.existsBucket("test-bucket-2"));
        }
    }

    @Nested
    class upload {
        @Test
        void upload_성공() {
            String key = "test-upload.txt";
            MockMultipartFile file = new MockMultipartFile(
                    "test-upload.txt", "test-upload.txt", "text/plain", "test content".getBytes()
            );

            String result = imageClientImpl.upload(file, key);

            assertEquals(key, result);
        }

        @Test
        void upload_중복키_실패() {
            String key = "test-upload.txt";
            MockMultipartFile file = new MockMultipartFile(
                    "test-upload.txt", "test-upload.txt", "text/plain", "test content".getBytes()
            );

            imageClientImpl.upload(file, key);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> imageClientImpl.upload(file, key));
            assertTrue(exception.getMessage().contains("DUPLICATE_IMAGE_KEY"));
        }
    }

    @Nested
    class find {
        @Test
        void find_성공() {
            String key = "test-find.txt";
            MockMultipartFile file = new MockMultipartFile(
                    "test-find.txt", "test-find.txt", "text/plain", "test content".getBytes()
            );

            imageClientImpl.upload(file, key);
            URL url = imageClientImpl.find(key);

            assertNotNull(url);
            assertTrue(url.toString().contains(key));
        }

        @Test
        void find_키_없음_실패() {
            String key = "test-find.txt";

            RuntimeException exception = assertThrows(RuntimeException.class, () -> imageClientImpl.find(key));
            assertTrue(exception.getMessage().contains("IMAGE_NOT_FOUND"));
        }
    }

    @Nested
    class delete {
        @Test
        void delete_성공() {
            String key = "test-delete.txt";
            MockMultipartFile file = new MockMultipartFile(
                    "test-delete.txt", "test-delete.txt", "text/plain", "test content".getBytes()
            );

            imageClientImpl.upload(file, key);
            imageClientImpl.delete(key);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> imageClientImpl.find(key));
            assertTrue(exception.getMessage().contains("IMAGE_NOT_FOUND"));
        }

        @Test
        void delete_키_없음_실패() {
            String key = "test-delete.txt";

            RuntimeException exception = assertThrows(RuntimeException.class, () -> imageClientImpl.delete(key));
            assertTrue(exception.getMessage().contains("IMAGE_NOT_FOUND"));
        }
    }


    @Nested
    class deleteAll {
        @Test
        void deleteAll_성공() {
            String key = "test-123";
            MockMultipartFile file = new MockMultipartFile(
                    "test-deleteAll.txt", "test-deleteAll.txt", "text/plain", "test content".getBytes()
            );

            imageClientImpl.upload(file, key);
            imageClientImpl.deleteAll();

            RuntimeException exception = assertThrows(RuntimeException.class, () -> imageClientImpl.find(key));
            assertTrue(exception.getMessage().contains("IMAGE_NOT_FOUND"));
        }
    }
}
