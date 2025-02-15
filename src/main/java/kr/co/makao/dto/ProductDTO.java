package kr.co.makao.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kr.co.makao.annotation.ValidFiles;
import kr.co.makao.entity.Category;
import kr.co.makao.entity.Product;
import kr.co.makao.entity.Store;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public record ProductDTO() {
    public record CreateRequest(
            @NotBlank String name,
            @NotBlank String description,
            @Min(0) int price,
            @Min(0) int count,
            @ValidFiles(maxSize = 1024 * 1024 * 30) @Size(min = 1, max = 10) MultipartFile[] images,
            boolean isActivated,
            @Min(0) int categoryId,
            @NotBlank String storeCode
    ) {
        public static Product toEntity(CreateRequest dto, Category category, Store store) {
            return Product.builder()
                    .name(dto.name())
                    .description(dto.description())
                    .price(dto.price())
                    .count(dto.count())
                    .isActivated(dto.isActivated())
                    .category(category)
                    .store(store)
                    .build();
        }
    }

    public record PatchRequest(
            Optional<@NotBlank String> name,
            Optional<@NotBlank String> description,
            Optional<@Min(0) Integer> price,
            Optional<@Min(0) Integer> count,
            Optional<@ValidFiles(maxSize = 1024 * 1024 * 30) @Size(min = 1, max = 10) MultipartFile[]> images,
            Optional<Boolean> isActivated,
            Optional<Integer> categoryId
    ) {
    }
}
