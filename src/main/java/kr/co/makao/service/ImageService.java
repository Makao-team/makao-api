package kr.co.makao.service;

import jakarta.transaction.Transactional;
import kr.co.makao.client.image.ImageClient;
import kr.co.makao.entity.Image;
import kr.co.makao.entity.Product;
import kr.co.makao.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ImageService {
    private final ImageClient imageClient;
    private final ImageRepository imageRepository;

    @Transactional
    public void uploadAll(MultipartFile[] images, Product product) {
        for (MultipartFile image : images) {
            upload(image, product);
        }
    }

    private void upload(MultipartFile image, Product product) {
        Image saved = imageRepository.saveAndFlush(Image.builder().product(product).build());
        imageClient.upload(image, saved.getKey());
    }

    public void deleteAll(Product product) {
        List<Image> images = imageRepository.findAllByProduct(product);
        imageRepository.deleteAll(images);
        for (Image image : images) {
            imageClient.delete(image.getKey());
        }
    }
}
