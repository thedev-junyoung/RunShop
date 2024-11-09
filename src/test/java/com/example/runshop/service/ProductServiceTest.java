package com.example.runshop.service;

import com.example.runshop.exception.product.ProductNotFoundException;
import com.example.runshop.model.dto.product.ProductDTO;
import com.example.runshop.model.dto.product.ProductPageDTO;
import com.example.runshop.model.dto.product.UpdateProductRequest;
import com.example.runshop.model.dto.product.AddProductRequest;
import com.example.runshop.model.entity.Product;
import com.example.runshop.model.enums.Category;
import com.example.runshop.model.vo.product.ProductDescription;
import com.example.runshop.model.vo.product.ProductName;
import com.example.runshop.model.vo.product.ProductPrice;
import com.example.runshop.repository.ProductRepository;
import com.example.runshop.utils.mapper.ProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private ProductMapper productMapper;

    @Test
    @DisplayName("상품을 성공적으로 등록한다")
    public void addProductSuccessfully() {
        // given
        AddProductRequest request = new AddProductRequest(
                new ProductName("나이키 운동화"),
                new ProductDescription("나이키 에어맥스"),
                new ProductPrice(BigDecimal.valueOf(100000)),
                Category.SHOES,
                "나이키"
        );
        Product product = Product.builder()
                .name(new ProductName(request.getName().value()))
                .description(new ProductDescription(request.getDescription().value()))
                .price(new ProductPrice(request.getPrice().value()))
                .category(request.getCategory())
                .brand(request.getBrand())
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(product);

        // when
        productService.addProduct(request);

        // then
        verify(productRepository, times(1)).save(any(Product.class));
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
        ProductDTO productDTO = ProductDTO.builder()
                .id(productId)
                .name(new ProductName("나이키 운동화"))
                .description(new ProductDescription("나이키 에어맥스"))
                .price(new ProductPrice(BigDecimal.valueOf(100000)))
                .category(Category.SHOES)
                .brand("나이키")
                .createdAt(LocalDateTime.now())
                .build();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.productToProductDTO(product)).thenReturn(productDTO);

        // when
        ProductDTO foundProduct = productService.getProduct(productId);

        // then
        assertEquals(new ProductName("나이키 운동화"), foundProduct.getName());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("상품 조회 실패 - 존재하지 않는 상품")
    public void failToGetProductWhenNotFound() {
        // given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(ProductNotFoundException.class, () -> productService.getProduct(productId));

        assertEquals("해당 상품을 찾을 수 없습니다.", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("상품을 성공적으로 수정한다")
    public void updateProductSuccessfully() {
        // given
        Long productId = 1L;
        Product existingProduct = Product.builder()
                .name(new ProductName("나이키 운동화"))
                .description(new ProductDescription("나이키 에어맥스"))
                .price(new ProductPrice(BigDecimal.valueOf(100000)))
                .category(Category.SHOES)
                .brand("나이키")
                .build();

        UpdateProductRequest updateRequest = new UpdateProductRequest(
                new ProductName("수정된 이름"),
                new ProductDescription("수정된 설명"),
                new ProductPrice(BigDecimal.valueOf(120000)),
                Category.SHOES,
                "나이키"
        );

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // when
        productService.updateProduct(productId, updateRequest);

        // then
        assertEquals("수정된 이름", existingProduct.getName().value());
        assertEquals("수정된 설명", existingProduct.getDescription().value());
        assertEquals(BigDecimal.valueOf(120000), existingProduct.getPrice().value());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("상품 수정 실패 - 존재하지 않는 상품")
    public void failToUpdateProductWhenNotFound() {
        // given
        Long productId = 1L;
        UpdateProductRequest updateRequest = new UpdateProductRequest(
                new ProductName("수정된 이름"),
                new ProductDescription("수정된 설명"),
                new ProductPrice(BigDecimal.valueOf(120000)),
                Category.SHOES,
                "나이키"
        );
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(productId, updateRequest));
    }

    @Test
    @DisplayName("상품을 성공적으로 삭제한다")
    public void deleteProductSuccessfully() {
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
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    @DisplayName("상품 삭제 실패 - 존재하지 않는 상품")
    public void failToDeleteProductWhenNotFound() {
        // given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(productId));
        verify(productRepository, times(0)).deleteById(productId);
    }

    @Test
    @DisplayName("상품을 비활성화 한다")
    public void disableProductSuccessfully() {
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
        productService.disabled(productId);

        // then
        assertFalse(existingProduct.isEnabled());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("전체 상품 조회 기능")
    public void getProductsSuccessfully() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Product product1 = Product.builder()
                .name(new ProductName("나이키 운동화"))
                .description(new ProductDescription("나이키 에어맥스"))
                .price(new ProductPrice(BigDecimal.valueOf(100000)))
                .category(Category.SHOES)
                .brand("나이키")
                .build();
        Product product2 = Product.builder()
                .name(new ProductName("아디다스 운동화"))
                .description(new ProductDescription("아디다스 울트라부스트"))
                .price(new ProductPrice(BigDecimal.valueOf(90000)))
                .category(Category.SHOES)
                .brand("아디다스")
                .build();

        Page<Product> products = new PageImpl<>(List.of(product1, product2), PageRequest.of(0, 10), 2);
        when(productRepository.findAll(pageable)).thenReturn(products);

        ProductDTO productDTO1 = ProductDTO.builder()
                .id(1L)
                .name(new ProductName("나이키 운동화"))
                .description(new ProductDescription("나이키 에어맥스"))
                .price(new ProductPrice(BigDecimal.valueOf(100000)))
                .category(Category.SHOES)
                .brand("나이키")
                .createdAt(LocalDateTime.now())
                .build();

        ProductDTO productDTO2 = ProductDTO.builder()
                .id(2L)
                .name(new ProductName("아디다스 운동화"))
                .description(new ProductDescription("아디다스 울트라부스트"))
                .price(new ProductPrice(BigDecimal.valueOf(90000)))
                .category(Category.SHOES)
                .brand("아디다스")
                .createdAt(LocalDateTime.now())
                .build();
        when(productMapper.productToProductDTO(product1)).thenReturn(productDTO1);
        when(productMapper.productToProductDTO(product2)).thenReturn(productDTO2);

        // when
        List<ProductDTO> result = productService.getProducts(pageable);

        // then
        assertNotNull(result);
        verify(productRepository, times(1)).findAll(pageable);
    }
}
