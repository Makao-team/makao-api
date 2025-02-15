package kr.co.makao.controller;

import kr.co.makao.dto.ProductDTO;
import kr.co.makao.exception.CommonResponse;
import kr.co.makao.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Validated
@RequestMapping("/products")
@RestController
public class ProductController {
    private final ProductService productService;

    @PostMapping
    ResponseEntity<CommonResponse<String>> createProduct(@ModelAttribute ProductDTO.CreateRequest dto) {
        return CommonResponse.success(productService.create(dto));
    }

    @PatchMapping("/{code}")
    ResponseEntity<CommonResponse<String>> patchProduct(@PathVariable("code") String code, @ModelAttribute ProductDTO.PatchRequest dto) {
        return CommonResponse.success(productService.patch(code, dto));
    }
}
