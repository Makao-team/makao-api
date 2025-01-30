package kr.co.makao.client;

import org.springframework.web.multipart.MultipartFile;
import java.net.URL;

public interface ImageClient {
    boolean exists(String key);
    String upload(MultipartFile file, String key);
    URL find(String key);
    void delete(String key);
}
