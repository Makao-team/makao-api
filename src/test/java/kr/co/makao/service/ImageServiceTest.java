package kr.co.makao.service;

import kr.co.makao.client.image.ImageClient;
import kr.co.makao.entity.Image;
import kr.co.makao.entity.Product;
import kr.co.makao.repository.ImageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {
    @InjectMocks
    private ImageService imageService;
    @Mock
    private ImageClient imageClient;
    @Mock
    private ImageRepository imageRepository;

    @Test
    void uploadAll_성공() {
        MultipartFile[] files = {
                mock(MultipartFile.class),
                mock(MultipartFile.class),
                mock(MultipartFile.class)
        };

        when(imageRepository.saveAndFlush(any(Image.class))).thenReturn(Image.builder().key("key").build());
        when(imageClient.upload(any(MultipartFile.class), anyString())).thenReturn("key");

        imageService.uploadAll(files, new Product());

        verify(imageRepository, times(3)).saveAndFlush(any(Image.class));
        verify(imageClient, times(3)).upload(any(MultipartFile.class), anyString());
    }

    @Test
    void deleteAll_성공() {
        List<Image> images = List.of(Image.builder().key("1").build(), Image.builder().key("2").build(), Image.builder().key("3").build());

        when(imageRepository.findAllByProduct(any(Product.class))).thenReturn(images);
        doNothing().when(imageRepository).deleteAll(images);
        doNothing().when(imageClient).delete(anyString());

        imageService.deleteAll(new Product());

        verify(imageRepository, times(1)).deleteAll(images);
        verify(imageClient, times(3)).delete(anyString());
    }
}