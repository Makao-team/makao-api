package kr.co.makao.client;


import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

public interface ImageClient {
    void createBucket(String bucketName);

    void deleteBucket(String bucketName);

    boolean existsBucket(String bucketName);

    boolean exists(String key);

    String upload(MultipartFile file, String key);

    URL find(String key);

    void delete(String key);

    void deleteAll();
}
