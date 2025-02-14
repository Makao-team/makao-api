package kr.co.makao.client.image;

import kr.co.makao.config.ClientConfig;
import kr.co.makao.config.MinioInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(initializers = MinioInitializer.class)
@SpringBootTest(classes = {ClientConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ImageClientImplTest {
    private final String KEY = "test-key";
    private final MultipartFile FILE = new MockMultipartFile("file", "test.txt", "text/plain", "Test".getBytes());
    @Autowired
    private ImageClient imageClient;

    @AfterEach
    void deleteAll() {
        imageClient.deleteAll();
    }

    @Nested
    class upload {
        @Test
        void upload_성공() {
            String result = imageClient.upload(FILE, KEY);
            assertEquals(KEY, result);
        }

        @Test
        void upload_중복키_실패() {
            imageClient.upload(FILE, KEY);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> imageClient.upload(FILE, KEY));
            assertTrue(exception.getMessage().contains("DUPLICATE_IMAGE_KEY"));
        }
    }

    @Nested
    class find {
        @Test
        void find_성공() {
            imageClient.upload(FILE, KEY);
            URL url = imageClient.find(KEY);

            assertNotNull(url);
            assertTrue(url.toString().contains(KEY));
        }

        @Test
        void find_키_없음_실패() {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> imageClient.find(KEY));
            assertTrue(exception.getMessage().contains("IMAGE_NOT_FOUND"));
        }
    }

    @Nested
    class delete {
        @Test
        void delete_성공() {
            imageClient.upload(FILE, KEY);
            imageClient.delete(KEY);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> imageClient.find(KEY));
            assertTrue(exception.getMessage().contains("IMAGE_NOT_FOUND"));
        }

        @Test
        void delete_키_없음_실패() {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> imageClient.delete(KEY));
            assertTrue(exception.getMessage().contains("IMAGE_NOT_FOUND"));
        }
    }


    @Nested
    class deleteAll {
        @Test
        void deleteAll_성공() {
            imageClient.upload(FILE, KEY);
            imageClient.deleteAll();

            RuntimeException exception = assertThrows(RuntimeException.class, () -> imageClient.find(KEY));
            assertTrue(exception.getMessage().contains("IMAGE_NOT_FOUND"));
        }
    }
}
