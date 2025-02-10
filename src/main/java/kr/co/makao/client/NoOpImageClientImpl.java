package kr.co.makao.client;

import kr.co.makao.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class NoOpImageClientImpl implements ImageClient {
    private final ConcurrentMap<String, String> storage = new ConcurrentHashMap<>();

    @Override
    public boolean exists(String key) {
        return storage.containsKey(key);
    }

    @Override
    public String upload(MultipartFile file, String key) {
        if (exists(key))
            throw ApiException.BAD_REQUEST.toException("DUPLICATE_IMAGE_KEY");
        storage.put(key, "mock-url/" + key);
        return key;
    }

    @Override
    public URL find(String key) {
        if (!exists(key))
            throw ApiException.BAD_REQUEST.toException("IMAGE_NOT_FOUND");
        try {
            return new URL("http://localhost:8080/" + key);
        } catch (MalformedURLException e) {
            log.error("IMAGE_FIND_FAILED", e);
            throw ApiException.IMAGE_SERVER_ERROR.toException("IMAGE_FIND_FAILED");
        }
    }

    @Override
    public void delete(String key) {
        if (!exists(key))
            throw ApiException.BAD_REQUEST.toException("IMAGE_NOT_FOUND");
        storage.remove(key);
    }

    @Override
    public void deleteAll() {
        storage.clear();
    }
}
