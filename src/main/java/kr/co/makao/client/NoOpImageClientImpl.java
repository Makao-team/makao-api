package kr.co.makao.client;

import org.springframework.web.multipart.MultipartFile;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NoOpImageClientImpl implements ImageClient {
    private final ConcurrentMap<String, String> storage = new ConcurrentHashMap<>();

    @Override
    public boolean exists(String key) {
        return storage.containsKey(key);
    }

    @Override
    public String upload(MultipartFile file, String key) {
        storage.put(key, "mock-url/" + key);
        return key;
    }

    @Override
    public URL find(String key) {
        if (!exists(key))
            throw new RuntimeException("IMAGE_NOT_FOUND");
        try {
            return new URL("http://localhost:8080/" + key);
        } catch (MalformedURLException e) {
            throw new RuntimeException("INVALID_URL", e);
        }
    }

    @Override
    public void delete(String key) {
        storage.remove(key);
    }
}
