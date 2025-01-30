package kr.co.makao.client;

public interface BucketClient {
    void createBucket(String bucketName);
    void deleteBucket(String bucketName);
    boolean existsBucket(String bucketName);
    void deleteAll();
}
