package kr.co.makao.client.image;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import kr.co.makao.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class ImageClientImpl implements ImageClient {
    private final MinioClient minio;
    private final String bucketName;
    private final int urlExpirationHours;

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
            throw CommonException.BAD_REQUEST.toException("DUPLICATE_IMAGE_KEY");
        try (var inputStream = file.getInputStream()) {
            minio.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(key)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            return key;
        } catch (Exception cause) {
            throw CommonException.IMAGE_SERVER_ERROR.toException("IMAGE_UPLOAD_FAILED", cause);
        }
    }

    @Override
    public URL find(String key) {
        if (!exists(key))
            throw CommonException.BAD_REQUEST.toException("IMAGE_NOT_FOUND");
        try {
            return new URL(minio.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(key)
                            .expiry(urlExpirationHours, TimeUnit.HOURS)
                            .build()
            ));
        } catch (Exception cause) {
            throw CommonException.IMAGE_SERVER_ERROR.toException("IMAGE_FIND_FAILED", cause);
        }
    }

    @Override
    public void delete(String key) {
        if (!exists(key))
            throw CommonException.BAD_REQUEST.toException("IMAGE_NOT_FOUND");
        try {
            minio.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(key)
                    .build());
        } catch (Exception cause) {
            throw CommonException.IMAGE_SERVER_ERROR.toException("IMAGE_DELETE_FAILED", cause);
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
        } catch (Exception cause) {
            throw CommonException.IMAGE_SERVER_ERROR.toException("IMAGE_DELETE_FAILED", cause);
        }
    }
}
