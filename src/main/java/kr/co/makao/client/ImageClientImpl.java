package kr.co.makao.client;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import kr.co.makao.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class ImageClientImpl implements ImageClient, BucketClient {
    private final MinioClient minio;
    private String bucketName;
    private int urlExpirationHours;

    @Override
    public void createBucket(String bucketName) {
        try {
            if (!existsBucket(bucketName)) {
                minio.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("BUCKET_CREATION_FAILED", e);
        }
    }

    @Override
    public void deleteBucket(String bucketName) {
        try {
            minio.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new RuntimeException("BUCKET_DELETION_FAILED", e);
        }
    }

    @Override
    public boolean existsBucket(String bucketName) {
        try {
            return minio.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new RuntimeException("BUCKET_NOT_FOUND", e);
        }
    }

    @Override
    public boolean exists(String key) {
        try {
            return minio.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(key)
                    .build()) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String upload(MultipartFile file, String key) {
        if (exists(key))
            throw ApiException.BAD_REQUEST.toException("DUPLICATE_IMAGE_KEY");
        try (var inputStream = file.getInputStream()) {
            minio.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(key)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            return key;
        } catch (Exception e) {
            throw new RuntimeException("IMAGE_UPLOAD_FAILED", e);
        }
    }

    @Override
    public URL find(String key) {
        if (!exists(key))
            throw ApiException.BAD_REQUEST.toException("IMAGE_NOT_FOUND");
        try {
            return new URL(minio.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(key)
                            .expiry(urlExpirationHours, TimeUnit.HOURS)
                            .build()
            ));
        } catch (Exception e) {
            throw new RuntimeException("IMAGE_FIND_FAILED", e);
        }
    }

    @Override
    public void delete(String key) {
        if (!exists(key))
            throw ApiException.BAD_REQUEST.toException("IMAGE_NOT_FOUND");
        try {
            minio.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(key)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("IMAGE_DELETE_FAILED", e);
        }
    }

    @Override
    public void deleteAll() {
        try {
            Iterable<Result<Item>> results = minio.listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .build());

            for (Result<Item> result : results) {
                delete(result.get().objectName());
            }
        } catch (Exception e) {
            throw new RuntimeException("IMAGE_DELETE_FAILED", e);
        }
    }
}
