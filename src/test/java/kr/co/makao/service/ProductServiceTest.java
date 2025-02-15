package kr.co.makao.service;

import kr.co.makao.dto.ProductDTO;
import kr.co.makao.entity.Category;
import kr.co.makao.entity.Product;
import kr.co.makao.entity.Store;
import kr.co.makao.exception.CommonExceptionImpl;
import kr.co.makao.repository.CategoryRepository;
import kr.co.makao.repository.ProductRepository;
import kr.co.makao.repository.StoreRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService productService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private ImageService imageService;
    @Mock
    private ProductRepository productRepository;

    @Nested
    class create {
        ProductDTO.CreateRequest dto = new ProductDTO.CreateRequest("name", "description", 1000, 10, new MultipartFile[0], true, 1, "storeCode");

        @Test
        void create_성공() {
            when(categoryRepository.findById(1)).thenReturn(Optional.of(new Category()));
            when(storeRepository.findOneByCode("storeCode")).thenReturn(Optional.of(new Store()));
            when(productRepository.saveAndFlush(any(Product.class))).thenReturn(Product.builder().code("code").build());
            doNothing().when(imageService).uploadAll(any(MultipartFile[].class), any(Product.class));

            String productCode = productService.create(dto);

            assertThat(productCode).isEqualTo("code");
        }

        @Test
        void create_카테고리_부재_실패() {
            when(categoryRepository.findById(1)).thenReturn(Optional.empty());

            CommonExceptionImpl exception = assertThrows(CommonExceptionImpl.class, () -> productService.create(dto));
            assertThat(exception.getMessage()).isEqualTo("CATEGORY_NOT_FOUND");
        }

        @Test
        void create_상점_부재_실패() {
            when(categoryRepository.findById(1)).thenReturn(Optional.of(new Category()));
            when(storeRepository.findOneByCode("storeCode")).thenReturn(Optional.empty());

            CommonExceptionImpl exception = assertThrows(CommonExceptionImpl.class, () -> productService.create(dto));
            assertThat(exception.getMessage()).isEqualTo("STORE_NOT_FOUND");
        }
    }

    @Nested
    class patch {
        String code = "code";
        Product product = Product.builder().code(code).build();
        ProductDTO.PatchRequest dto = new ProductDTO.PatchRequest(Optional.of("name"), Optional.of("description"), Optional.of(1000), Optional.of(10), Optional.of(new MultipartFile[]{}), Optional.of(true), Optional.of(1));

        @Test
        void patch_성공() {
            when(productRepository.findOneByCode(anyString())).thenReturn(Optional.of(product));
            when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(new Category()));
            doNothing().when(imageService).deleteAll(any(Product.class));
            doNothing().when(imageService).uploadAll(any(MultipartFile[].class), any(Product.class));
            when(productRepository.saveAndFlush(any(Product.class))).thenReturn(product);

            String productCode = productService.patch(code, dto);

            assertThat(productCode).isEqualTo(code);
        }

        @Test
        void patch_상품_부재_실패() {
            when(productRepository.findOneByCode(anyString())).thenReturn(Optional.empty());

            CommonExceptionImpl exception = assertThrows(CommonExceptionImpl.class, () -> productService.patch(code, dto));
            assertThat(exception.getMessage()).isEqualTo("PRODUCT_NOT_FOUND");
        }

        @Test
        void patch_활성화된_상품_실패() {
            product.setActivated(true);
            when(productRepository.findOneByCode(anyString())).thenReturn(Optional.of(product));

            CommonExceptionImpl exception = assertThrows(CommonExceptionImpl.class, () -> productService.patch(code, dto));
            assertThat(exception.getMessage()).isEqualTo("PRODUCT_ALREADY_ACTIVATED");
            product.setActivated(false);
        }

        @Test
        void patch_카테고리_부재_실패() {
            when(productRepository.findOneByCode(anyString())).thenReturn(Optional.of(product));
            when(categoryRepository.findById(anyInt())).thenReturn(Optional.empty());

            CommonExceptionImpl exception = assertThrows(CommonExceptionImpl.class, () -> productService.patch(code, dto));
            assertThat(exception.getMessage()).isEqualTo("CATEGORY_NOT_FOUND");
        }
    }
}