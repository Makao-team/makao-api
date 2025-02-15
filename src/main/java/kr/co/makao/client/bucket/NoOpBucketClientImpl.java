package kr.co.makao.client.bucket;

public class NoOpBucketClientImpl implements BucketClient {
    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public void create() {
    }

    @Override
    public void delete() {

    }
}
