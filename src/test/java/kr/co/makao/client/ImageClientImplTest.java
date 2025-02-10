package kr.co.makao.client;

import kr.co.makao.MakaoApplication;
import kr.co.makao.config.MinioInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MakaoApplication.class)
@ContextConfiguration(initializers = MinioInitializer.class)
@EnableAutoConfiguration(exclude = {HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ImageClientImplTest {
    private final String KEY = "test-key";
    private final MultipartFile FILE = new MockMultipartFile("file", "test.txt", "text/plain", "Test".getBytes());
    @Autowired
    private ImageClient imageClientImpl;

    @AfterEach
    void deleteAll() {
        imageClientImpl.deleteAll();
    }

    @Nested
    class upload {
        @Test
        void upload_성공() {
            String result = imageClientImpl.upload(FILE, KEY);
            assertEquals(KEY, result);
        }

        @Test
        void upload_중복키_실패() {
            imageClientImpl.upload(FILE, KEY);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> imageClientImpl.upload(FILE, KEY));
            assertTrue(exception.getMessage().contains("DUPLICATE_IMAGE_KEY"));
        }
    }

    @Nested
    class find {
        @Test
        void find_성공() {
            imageClientImpl.upload(FILE, KEY);
            URL url = imageClientImpl.find(KEY);

            assertNotNull(url);
            assertTrue(url.toString().contains(KEY));
        }

        @Test
        void find_키_없음_실패() {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> imageClientImpl.find(KEY));
            assertTrue(exception.getMessage().contains("IMAGE_NOT_FOUND"));
        }
    }

    @Nested
    class delete {
        @Test
        void delete_성공() {
            imageClientImpl.upload(FILE, KEY);
            imageClientImpl.delete(KEY);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> imageClientImpl.find(KEY));
            assertTrue(exception.getMessage().contains("IMAGE_NOT_FOUND"));
        }

        @Test
        void delete_키_없음_실패() {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> imageClientImpl.delete(KEY));
            assertTrue(exception.getMessage().contains("IMAGE_NOT_FOUND"));
        }
    }


    @Nested
    class deleteAll {
        @Test
        void deleteAll_성공() {
            imageClientImpl.upload(FILE, KEY);
            imageClientImpl.deleteAll();

            RuntimeException exception = assertThrows(RuntimeException.class, () -> imageClientImpl.find(KEY));
            assertTrue(exception.getMessage().contains("IMAGE_NOT_FOUND"));
        }
    }
}
