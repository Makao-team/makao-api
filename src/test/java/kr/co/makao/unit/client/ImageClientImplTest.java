package kr.co.makao.unit.client;

import kr.co.makao.client.ImageClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.net.URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ImageClientImplTest {

    @Autowired
    private ImageClient imageClient;

    @Test
    void upload_성공() {
        // Given
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Test content".getBytes()
        );
        String key = "test-key";

        // When
        String result = imageClient.upload(file, key);

        // Then
        assertEquals(key, result);
        assertTrue(imageClient.exists(key));
    }

    @Test
    void exists_성공() {
        // Given
        String key = "existing-key";
        imageClient.upload(new MockMultipartFile("file", "test.txt", "text/plain", "Test".getBytes()), key);

        // When & Then
        assertTrue(imageClient.exists(key));
    }

    @Test
    void exists_실패() {
        assertFalse(imageClient.exists("non-existing-key"));
    }

    @Test
    void find_성공() throws Exception {
        // Given
        String key = "valid-key";
        imageClient.upload(new MockMultipartFile("file", "test.txt", "text/plain", "Test".getBytes()), key);

        // When
        URL url = imageClient.find(key);

        // Then
        assertNotNull(url);
        assertThat(url.toString()).isEqualTo("http://localhost:8080/" + key);
    }

    @Test
    void find_실패_이미지_없음() {
        // Given
        String key = "non-existing-key";

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> imageClient.find(key));
        assertThat(exception.getMessage()).contains("IMAGE_NOT_FOUND");
    }

    @Test
    void delete_성공() {
        // Given
        String key = "delete-key";
        imageClient.upload(new MockMultipartFile("file", "test.txt", "text/plain", "Test".getBytes()), key);
        assertTrue(imageClient.exists(key));

        // When
        imageClient.delete(key);

        // Then
        assertFalse(imageClient.exists(key));
    }
}
