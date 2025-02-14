package kr.co.makao.client.bucket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@ExtendWith(MockitoExtension.class)
class BucketClientImplTest {
    BucketClient bucketClient = new NoOpBucketClientImpl();

    @Test
    void exists_성공() {
        boolean result = bucketClient.exists();

        assertThat(result).isFalse();
    }

    @Test
    void create_성공() {
        bucketClient.create();
    }

    @Test
    void delete_성공() {
        bucketClient.delete();
    }
}