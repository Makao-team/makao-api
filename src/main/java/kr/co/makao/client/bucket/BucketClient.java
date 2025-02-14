package kr.co.makao.client.bucket;

public interface BucketClient {
    boolean exists();

    void create();

    void delete();
}
