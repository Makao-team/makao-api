package kr.co.makao.client.bucket;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.RemoveBucketArgs;
import kr.co.makao.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class BucketClientImpl implements BucketClient {
    private final MinioClient minio;
    private final String bucketName;

    @Override
    public boolean exists() {
        try {
            return minio.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception cause) {
            throw CommonException.IMAGE_SERVER_ERROR.toException("BUCKET_ERROR", cause);
        }
    }

    @Override
    public void create() {
        try {
            if (!exists()) {
                minio.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception cause) {
            throw CommonException.IMAGE_SERVER_ERROR.toException("BUCKET_CREATION_FAILED", cause);
        }
    }

    @Override
    public void delete() {
        try {
            minio.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception cause) {
            throw CommonException.IMAGE_SERVER_ERROR.toException("BUCKET_DELETION_FAILED", cause);
        }
    }
}
