package kr.co.makao.service;

import jakarta.transaction.Transactional;
import kr.co.makao.dto.ProductDTO;
import kr.co.makao.entity.Category;
import kr.co.makao.entity.Product;
import kr.co.makao.entity.Store;
import kr.co.makao.exception.CommonException;
import kr.co.makao.repository.CategoryRepository;
import kr.co.makao.repository.ProductRepository;
import kr.co.makao.repository.StoreRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

// [Todo] ModelMapper 사용해보기
@AllArgsConstructor
@Service
public class ProductService {
    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;
    private final ImageService imageService;
    private final ProductRepository productRepository;

    @Transactional
    public String create(ProductDTO.CreateRequest dto) {
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> CommonException.BAD_REQUEST.toException("CATEGORY_NOT_FOUND"));

        Store store = storeRepository.findOneByCode(dto.storeCode())
                .orElseThrow(() -> CommonException.BAD_REQUEST.toException("STORE_NOT_FOUND"));

        Product product = ProductDTO.CreateRequest.toEntity(dto, category, store);
        Product saved = productRepository.saveAndFlush(product);

        imageService.uploadAll(dto.images(), product);

        return saved.getCode();
    }

    @Transactional
    public String patch(String code, ProductDTO.PatchRequest dto) {
        Product product = productRepository.findOneByCode(code.toUpperCase()).orElseThrow(() -> CommonException.BAD_REQUEST.toException("PRODUCT_NOT_FOUND"));
        if (product.isActivated())
            throw CommonException.BAD_REQUEST.toException("PRODUCT_ALREADY_ACTIVATED");

        dto.name().ifPresent(product::setName);
        dto.description().ifPresent(product::setDescription);
        dto.price().ifPresent(product::setPrice);
        dto.count().ifPresent(product::setCount);
        dto.isActivated().ifPresent(product::setActivated);

        dto.categoryId().ifPresent(id ->
                categoryRepository.findById(id).orElseThrow(() -> CommonException.BAD_REQUEST.toException("CATEGORY_NOT_FOUND"))
        );

        dto.images().ifPresent(images -> {
            imageService.deleteAll(product);
            imageService.uploadAll(images, product);
        });

        return productRepository.saveAndFlush(product).getCode();
    }
}
