package kr.co.makao.unit.client;

import io.minio.*;
import io.minio.messages.Item;
import kr.co.makao.client.MinIOImageClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(MockitoExtension.class)
class MinIOImageClientTest {
    private final String bucketName = "test-bucket";
    private final String imageKey = "test-1234";
    @InjectMocks
    private MinIOImageClient minIOImageClient;
    @Mock
    private MinioClient minioClientMock;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(minIOImageClient, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(minIOImageClient, "urlExpirationHours", 1);
    }

    @Nested
    class createBucket {
        @Test
        void createBucket_성공() throws Exception {
            doNothing().when(minioClientMock).makeBucket(any(MakeBucketArgs.class));

            minIOImageClient.createBucket(bucketName);
        }

        @Test
        void createBucket_실패() throws Exception {
            doThrow(new RuntimeException("Bucket creation failed"))
                    .when(minioClientMock).makeBucket(any(MakeBucketArgs.class));

            Exception exception = assertThrows(RuntimeException.class, () -> minIOImageClient.createBucket(bucketName));
            assertEquals("BUCKET_CREATION_FAILED", exception.getMessage());
        }
    }

    @Nested
    class deleteBucket {
        @Test
        void deleteBucket_성공() throws Exception {
            doNothing().when(minioClientMock).removeBucket(any(RemoveBucketArgs.class));

            minIOImageClient.deleteBucket(bucketName);
        }

        @Test
        void deleteBucket_실패() throws Exception {
            doThrow(new RuntimeException("Bucket deletion failed"))
                    .when(minioClientMock).removeBucket(any(RemoveBucketArgs.class));

            Exception exception = assertThrows(RuntimeException.class, () -> minIOImageClient.deleteBucket(bucketName));
            assertEquals("BUCKET_DELETION_FAILED", exception.getMessage());
        }
    }

    @Nested
    class existsBucket {
        @Test
        void existsBucket_성공() throws Exception {
            when(minioClientMock.bucketExists(any(BucketExistsArgs.class)))
                    .thenReturn(true);

            assertTrue(minIOImageClient.existsBucket(bucketName));
        }

        @Test
        void existsBucket_실패() throws Exception {
            when(minioClientMock.bucketExists(any(BucketExistsArgs.class)))
                    .thenThrow(new RuntimeException("Bucket not found"));

            Exception exception = assertThrows(RuntimeException.class, () -> minIOImageClient.existsBucket(bucketName));
            assertEquals("BUCKET_NOT_FOUND", exception.getMessage());
        }
    }

    @Nested
    class exists {
        @Test
        void exists_true_성공() throws Exception {
            when(minioClientMock.statObject(any(StatObjectArgs.class)))
                    .thenReturn(mock(StatObjectResponse.class));

            assertTrue(minIOImageClient.exists(imageKey));
        }

        @Test
        void exists_에러_후_false_성공() throws Exception {
            when(minioClientMock.statObject(any(StatObjectArgs.class)))
                    .thenThrow(new RuntimeException("Object not found"));

            assertFalse(minIOImageClient.exists(imageKey));
        }
    }

    @Nested
    class upload {
        final MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Test content".getBytes()
        );

        @Test
        void upload_성공() throws Exception {
            when(minioClientMock.statObject(any(StatObjectArgs.class)))
                    .thenThrow(new RuntimeException("Object not found"));
            when(minioClientMock.putObject(any(PutObjectArgs.class)))
                    .thenReturn(mock(ObjectWriteResponse.class));

            String result = minIOImageClient.upload(file, imageKey);

            assertEquals(imageKey, result);
        }

        @Test
        void upload_중복키_실패() throws Exception {
            when(minioClientMock.statObject(any(StatObjectArgs.class)))
                    .thenReturn(mock(StatObjectResponse.class));

            Exception exception = assertThrows(RuntimeException.class, () -> minIOImageClient.upload(file, imageKey));
            assertEquals("DUPLICATE_IMAGE_KEY", exception.getMessage());
        }

        @Test
        void upload_실패() throws Exception {
            doThrow(new RuntimeException("Upload failed"))
                    .when(minioClientMock).putObject(any(PutObjectArgs.class));

            Exception exception = assertThrows(RuntimeException.class, () -> minIOImageClient.upload(file, imageKey));
            assertEquals("IMAGE_UPLOAD_FAILED", exception.getMessage());
        }
    }

    @Nested
    class find {
        @Test
        void find_성공() throws Exception {
            String presignedUrl = "http://localhost:9000/test-folder/test.txt";

            when(minioClientMock.statObject(any(StatObjectArgs.class)))
                    .thenReturn(mock(StatObjectResponse.class));
            when(minioClientMock.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                    .thenReturn(presignedUrl);

            URL result = minIOImageClient.find(imageKey);

            assertNotNull(result);
            assertEquals(presignedUrl, result.toString());
        }

        @Test
        void find_이미지_없음_실패() throws Exception {
            when(minioClientMock.statObject(any(StatObjectArgs.class)))
                    .thenThrow(new RuntimeException("Object not found"));

            Exception exception = assertThrows(RuntimeException.class, () -> minIOImageClient.find(imageKey));
            assertEquals("IMAGE_NOT_FOUND", exception.getMessage());
        }

        @Test
        void find_실패() throws Exception {
            when(minioClientMock.statObject(any(StatObjectArgs.class)))
                    .thenReturn(mock(StatObjectResponse.class));
            when(minioClientMock.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                    .thenThrow(new RuntimeException("Presigned URL not found"));
            Exception exception = assertThrows(RuntimeException.class, () -> minIOImageClient.find(imageKey));
            assertEquals("IMAGE_FIND_FAILED", exception.getMessage());
        }
    }

    @Nested
    class delete {
        @Test
        void delete_성공() throws Exception {
            when(minioClientMock.statObject(any(StatObjectArgs.class)))
                    .thenReturn(mock(StatObjectResponse.class));
            doNothing().when(minioClientMock).removeObject(any(RemoveObjectArgs.class));

            minIOImageClient.delete(imageKey);
        }

        @Test
        void delete_이미지_없음_실패() throws Exception {
            when(minioClientMock.statObject(any(StatObjectArgs.class)))
                    .thenThrow(new RuntimeException("Object not found"));

            Exception exception = assertThrows(RuntimeException.class, () -> minIOImageClient.delete(imageKey));
            assertEquals("IMAGE_NOT_FOUND", exception.getMessage());
        }

        @Test
        void delete_실패() throws Exception {
            when(minioClientMock.statObject(any(StatObjectArgs.class)))
                    .thenReturn(mock(StatObjectResponse.class));
            doThrow(new RuntimeException("Delete failed"))
                    .when(minioClientMock).removeObject(any(RemoveObjectArgs.class));

            Exception exception = assertThrows(RuntimeException.class, () -> minIOImageClient.delete(imageKey));
            assertEquals("IMAGE_DELETE_FAILED", exception.getMessage());
        }
    }

    @Nested
    class deleteAll {
        @Test
        void deleteAll_성공() throws Exception {
            Item itemMock = mock(Item.class);
            when(itemMock.objectName()).thenReturn("test-image.jpg");

            Result<Item> resultMock = mock(Result.class);
            when(resultMock.get()).thenReturn(itemMock);

            when(minioClientMock.listObjects(any(ListObjectsArgs.class)))
                    .thenReturn(List.of(resultMock));

            when(minioClientMock.statObject(any(StatObjectArgs.class)))
                    .thenReturn(mock(StatObjectResponse.class));

            doNothing().when(minioClientMock).removeObject(any(RemoveObjectArgs.class));

            minIOImageClient.deleteAll();
        }

        @Test
        void deleteAll_실패() throws Exception {
            when(minioClientMock.listObjects(any(ListObjectsArgs.class)))
                    .thenThrow(new RuntimeException("List objects failed"));

            Exception exception = assertThrows(RuntimeException.class, () -> minIOImageClient.deleteAll());
            assertEquals("IMAGE_DELETE_FAILED", exception.getMessage());
        }
    }
}
