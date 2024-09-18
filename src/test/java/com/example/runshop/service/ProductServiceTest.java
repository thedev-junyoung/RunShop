package com.example.runshop.service;

import com.example.runshop.exception.product.CharactersArrangeException;
import com.example.runshop.exception.product.PriceNegativeException;
import com.example.runshop.exception.product.ProductNotFoundException;
import com.example.runshop.model.dto.product.ProductDTO;
import com.example.runshop.model.dto.product.UpdateProductRequest;
import com.example.runshop.model.dto.product.AddProductRequest;
import com.example.runshop.model.enums.Category;
import com.example.runshop.model.vo.product.ProductDescription;
import com.example.runshop.model.vo.product.ProductName;
import com.example.runshop.model.vo.product.ProductPrice;
import com.example.runshop.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.example.runshop.model.entity.Product;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;  // 실제 ProductService 빈을 사용

    @MockBean
    private ProductRepository productRepository;

    @Test
    @WithMockUser(roles = "SELLER")
    @DisplayName("상품을 성공적으로 등록한다")
    public void addProductSuccessfully() {
        // given
        AddProductRequest request = new AddProductRequest(
                new ProductName("나이키 운동화"),  // VO 사용
                new ProductDescription("나이키 에어맥스"),  // VO 사용
                new ProductPrice(BigDecimal.valueOf(100000)),  // VO 사용
                Category.SHOES,
                "나이키"
        );

        // when
        productService.addProduct(request);

        // then
        verify(productRepository, times(1)).save(any(Product.class));  // 저장 여부 검증
    }
    @Test
    @DisplayName("상품 등록 실패 - 이름이 없는 경우")
    public void failToAddProductDueToMissingName() {
        // when & then
        CharactersArrangeException exception = assertThrows(CharactersArrangeException.class, () -> {
            AddProductRequest request = new AddProductRequest(
                    new ProductName(""),  // 빈 이름으로 VO 생성, 예외 발생 예상
                    new ProductDescription("나이키 에어맥스"),
                    new ProductPrice(BigDecimal.valueOf(100000)),
                    Category.SHOES,
                    "나이키"
            );

            productService.addProduct(request);
        });

        // 예외 메시지 확인
        assertEquals("제품 이름은 3~255자 사이여야 합니다.", exception.getMessage());
    }
    @Test
    @DisplayName("상품 등록 실패 - 가격이 음수인 경우")
    public void failToAddProductDueToNegativePrice() {
        // when & then
        PriceNegativeException exception = assertThrows(PriceNegativeException.class, () -> {
            AddProductRequest request = new AddProductRequest(
                    new ProductName("나이키 운동화"),
                    new ProductDescription("나이키 에어맥스"),
                    new ProductPrice(BigDecimal.valueOf(-100000)),  // 음수 가격으로 VO 생성
                    Category.SHOES,
                    "나이키"
            );

            productService.addProduct(request);
        });

        // 예외 메시지 확인
        assertEquals("Price cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("상품을 성공적으로 조회한다")
    public void getProductSuccessfully() {
        // given
        Long productId = 1L;
        Product product = Product.builder()
                .name(new ProductName("나이키 운동화"))
                .description(new ProductDescription("나이키 에어맥스"))
                .price(new ProductPrice(BigDecimal.valueOf(100000)))
                .category(Category.SHOES)
                .brand("나이키")
                .build();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        ProductDTO foundProduct = productService.getProduct(productId);

        // then
        assertEquals("나이키 운동화", foundProduct.getName().value());  // VO에서 값 가져옴
        verify(productRepository, times(1)).findById(productId);
    }
    @Test
    @DisplayName("상품 조회 실패 - 존재하지 않는 상품")
    public void failToGetProductWhenNotFound() {
        // given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty()); // 상품이 없을 때

        // when & then
        Exception exception = assertThrows(ProductNotFoundException.class, () -> productService.getProduct(productId));

        assertEquals("해당 상품을 찾을 수 없습니다.", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @WithMockUser(roles = "SELLER")
    @DisplayName("권한을 가지고 상품을 성공적으로 수정한다")
    public void updateProductSuccessfullyWithProperRole() {
        // given
        Long productId = 1L;
        Product existingProduct = Product.builder()
                .name(new ProductName("나이키 운동화"))
                .description(new ProductDescription("나이키 에어맥스"))
                .price(new ProductPrice(BigDecimal.valueOf(100000)))
                .category(Category.SHOES)
                .brand("나이키")
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        UpdateProductRequest updateRequest = new UpdateProductRequest(
                new ProductName("수정된 이름"),
                new ProductDescription("수정된 설명"),
                new ProductPrice(BigDecimal.valueOf(120000)),
                Category.SHOES,
                "나이키"
        );

        // when
        productService.updateProduct(productId, updateRequest);

        // then
        assertEquals("수정된 이름", existingProduct.getName().value());  // VO에서 값 가져오기
        verify(productRepository, times(1)).save(existingProduct);
    }
    @Test
    @DisplayName("권한이 없으면 상품 수정을 실패한다")
    public void failToUpdateProductWithoutProperRole() {
        // given
        Long productId = 1L;
        Product existingProduct = Product.builder()
                .name(new ProductName("나이키 운동화"))
                .description(new ProductDescription("나이키 에어맥스"))
                .price(new ProductPrice(BigDecimal.valueOf(100000)))
                .category(Category.SHOES)
                .brand("나이키")
                .build();
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        UpdateProductRequest updateRequest = new UpdateProductRequest(
                new ProductName("수정된 이름"),
                new ProductDescription("수정된 설명"),
                new ProductPrice(BigDecimal.valueOf(120000)),
                Category.SHOES,
                "나이키"
        );

        // when & then
        Exception exception = assertThrows(SecurityException.class, () -> {
            productService.updateProduct(productId, updateRequest);  // 권한이 없는 사용자
        });

        assertEquals("권한이 없습니다.", exception.getMessage());
        verify(productRepository, times(0)).save(existingProduct); // 저장되지 않음
    }

    @Test
    @WithMockUser(roles = "SELLER")
    @DisplayName("권한을 가지고 상품을 성공적으로 삭제한다")
    public void deleteProductSuccessfullyWithProperRole() {
        // given
        Long productId = 1L;
        Product existingProduct = Product.builder()
                .name(new ProductName("나이키 운동화"))
                .description(new ProductDescription("나이키 에어맥스"))
                .price(new ProductPrice(BigDecimal.valueOf(100000)))
                .category(Category.SHOES)
                .brand("나이키")
                .build();
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // when
        productService.deleteProduct(productId);

        // then
        verify(productRepository, times(1)).deleteById(productId); // 삭제가 정상적으로 이루어졌는지 검증
    }

    @Test
    @DisplayName("권한이 없으면 상품 삭제를 실패한다")
    public void failToDeleteProductWithoutProperRole() {
        // given
        Long productId = 1L;
        Product existingProduct = Product.builder()
                .name(new ProductName("나이키 운동화"))
                .description(new ProductDescription("나이키 에어맥스"))
                .price(new ProductPrice(BigDecimal.valueOf(100000)))
                .category(Category.SHOES)
                .brand("나이키")
                .build();
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // when & then
        assertThrows(SecurityException.class, () -> {
            productService.deleteProduct(productId); // 권한이 없는 사용자
        });

        verify(productRepository, times(0)).deleteById(productId); // 삭제되지 않음
    }
}
