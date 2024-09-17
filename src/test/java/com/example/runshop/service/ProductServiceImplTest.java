package com.example.runshop.service;

import com.example.runshop.exception.product.ProductNotFoundException;
import com.example.runshop.model.dto.product.ProductDTO;
import com.example.runshop.model.dto.product.UpdateProductRequest;
import com.example.runshop.model.dto.product.AddProductRequest;
import com.example.runshop.model.enums.Category;
import com.example.runshop.repository.ProductRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.example.runshop.model.entity.Product;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProductServiceImplTest {

    @Autowired
    private ProductService productService;  // 실제 ProductService 빈을 사용

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private Validator validator;

    @Test
    @WithMockUser(roles = "SELLER")  // SELLER 권한을 가진 사용자로 테스트
    public void 상품등록() {
        // given
        AddProductRequest request = new AddProductRequest(
                "나이키운동화",
                "나이키 에어맥스",
                100000,
                Category.SHOES,
                "나이키"
        );
        // when
        productService.addProduct(request);

        // then
        verify(productRepository, times(1)).save(any(Product.class)); // 상품이 저장되었는지 검증
    }

    @Test
    public void 상품등록_실패_이름없음() {
        // given
        AddProductRequest request = new AddProductRequest(
                "",  // 이름 없음
                "나이키 에어맥스",
                100000,
                Category.SHOES,
                "나이키"
        );

        // when & then
        Set<ConstraintViolation<AddProductRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());  // 검증에 실패하는지 확인
    }

    @Test
    public void 상품등록_실패_가격_음수() {
        // given
        AddProductRequest request = new AddProductRequest(
                "나이키운동화",
                "나이키 에어맥스",
                -1000, // 음수 가격
                Category.SHOES,
                "나이키"
        );

        // when & then
        Set<ConstraintViolation<AddProductRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());  // 검증에 실패하는지 확인
    }

    @Test
    public void 상품조회_성공() {
        // given
        Long productId = 1L;
        Product product = Product.builder()
                .name("나이키 운동화")
                .description("나이키 에어맥스")
                .price(100000)
                .category(Category.SHOES)
                .brand("나이키")
                .build();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        ProductDTO foundProduct = productService.getProduct(productId);

        // then
        assertEquals("나이키 운동화", foundProduct.getName());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    public void 상품조회_상품없음() {
        // given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());  // 상품이 없을 때

        // when & then
        Exception exception = assertThrows(ProductNotFoundException.class, () -> productService.getProduct(productId));

        assertEquals("해당 상품을 찾을 수 없습니다.", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @WithMockUser(roles = "SELLER")
    public void 상품수정_권한성공() {
        // given
        Long productId = 1L;
        Product existingProduct = Product.builder()
                .name("나이키 운동화")
                .description("나이키 에어맥스")
                .price(100000)
                .category(Category.SHOES)
                .brand("나이키")
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        UpdateProductRequest updateRequest = new UpdateProductRequest(
                "수정된 이름",
                "수정된 설명",
                120000,
                Category.SHOES,
                "나이키"
        );
        // when
        productService.updateProduct(productId, updateRequest);

        // then
        assertEquals("수정된 이름", existingProduct.getName());
        verify(productRepository, times(1)).save(existingProduct);
    }

    @Test
    public void 상품수정_권한실패() {
        // given
        Long productId = 1L;
        Product existingProduct = Product.builder()
                .name("나이키 운동화")
                .description("나이키 에어맥스")
                .price(100000)
                .category(Category.SHOES)
                .brand("나이키")
                .build();
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        UpdateProductRequest updateRequest = new UpdateProductRequest(
                "수정된 이름",
                "수정된 설명",
                120000,
                Category.SHOES,
                "나이키"
        );

        // when & then
        Exception exception = assertThrows(SecurityException.class, () -> {
            productService.updateProduct(productId, updateRequest);  // 권한이 없는 사용자
        });

        assertEquals("권한이 없습니다.", exception.getMessage());
        verify(productRepository, times(0)).save(existingProduct);  // 저장되지 않음
    }

    @Test
    @WithMockUser(roles = "SELLER")
    public void 상품삭제_권한성공() {
        // given
        Long productId = 1L;
        Product existingProduct = Product.builder()
                .name("나이키 운동화")
                .description("나이키 에어맥스")
                .price(100000)
                .category(Category.SHOES)
                .brand("나이키")
                .build();
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // when
        productService.deleteProduct(productId);

        // then
        verify(productRepository, times(1)).deleteById(productId);  // 삭제가 정상적으로 이루어졌는지 검증
    }

    @Test
    public void 상품삭제_권한실패() {
        // given
        Long productId = 1L;
        Product existingProduct = Product.builder()
                .name("나이키 운동화")
                .description("나이키 에어맥스")
                .price(100000)
                .category(Category.SHOES)
                .brand("나이키")
                .build();
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // when & then
        assertThrows(SecurityException.class, () -> {
            productService.deleteProduct(productId);  // 권한이 없는 사용자
        });

        verify(productRepository, times(0)).deleteById(productId);
    }
}
