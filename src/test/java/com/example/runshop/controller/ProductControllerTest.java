package com.example.runshop.controller;

import com.example.runshop.model.dto.product.ProductDTO;
import com.example.runshop.model.dto.product.UpdateProductRequest;
import com.example.runshop.model.dto.product.AddProductRequest;
import com.example.runshop.model.dto.review.AddReviewRequest;
import com.example.runshop.model.dto.review.ReviewDTO;
import com.example.runshop.model.enums.Category;
import com.example.runshop.model.vo.product.ProductDescription;
import com.example.runshop.model.vo.product.ProductName;
import com.example.runshop.model.vo.product.ProductPrice;
import com.example.runshop.model.vo.review.ReviewContent;
import com.example.runshop.model.vo.review.ReviewRating;
import com.example.runshop.service.ProductService;
import com.example.runshop.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private ReviewService reviewService;
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService, reviewService)).build();
    }

    @Test
    @DisplayName("상품 등록 API 성공")
    public void SubmitProduct_API_Success() throws Exception {
        AddProductRequest request = new AddProductRequest(
                new ProductName("나이키 운동화"),
                new ProductDescription("나이키 에어맥스"),
                new ProductPrice(BigDecimal.valueOf(100000)),
                Category.SHOES,
                "나이키");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("role", "SELLER")  // role 파라미터 추가
                        .content("{\"name\":\"나이키 운동화\", \"description\":\"나이키 에어맥스\", \"price\":100000, \"category\":\"SHOES\", \"brand\":\"나이키\", \"role\":\"SELLER\"}")
                )
                .andExpect(status().isOk());

        verify(productService, times(1)).addProduct(any(AddProductRequest.class));
    }
    @Test
    @DisplayName("상품 목록 페이징 조회 API 성공")
    public void GetProducts_API_Success() throws Exception {
        // Given: 페이징 처리된 상품 목록
        Long productId1 = 1L;
        Long productId2 = 2L;

        ProductDTO product1 = ProductDTO.builder()
                .id(productId1)
                .name(new ProductName("나이키 운동화"))
                .description(new ProductDescription("나이키 에어맥스"))
                .price(new ProductPrice(BigDecimal.valueOf(100000)))
                .category(Category.SHOES)
                .brand("나이키")
                .build();

        ProductDTO product2 = ProductDTO.builder()
                .id(productId2)
                .name(new ProductName("아디다스 운동화"))
                .description(new ProductDescription("아디다스 울트라부스트"))
                .price(new ProductPrice(BigDecimal.valueOf(120000)))
                .category(Category.SHOES)
                .brand("아디다스")
                .build();

        // 페이징 처리된 리스트 생성
        List<ProductDTO> products = List.of(product1, product2);
        Page<ProductDTO> productPage = new PageImpl<>(products, PageRequest.of(0, 10), products.size());

        // When: productService의 페이징 처리된 상품 목록 반환 설정
        when(productService.getProducts(any(Pageable.class))).thenReturn(productPage);

        // Then: 페이징된 상품 목록을 성공적으로 조회하는지 확인
        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].name").value("나이키 운동화"))
                .andExpect(jsonPath("$.data.content[0].description").value("나이키 에어맥스"))
                .andExpect(jsonPath("$.data.content[1].name").value("아디다스 운동화"))
                .andExpect(jsonPath("$.data.content[1].description").value("아디다스 울트라부스트"))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(1));

        // Verify: 서비스 호출이 정확하게 이루어졌는지 검증
        verify(productService, times(1)).getProducts(any(Pageable.class));
    }

    @Test
    @DisplayName("상품 조회 API 성공")
    public void GetProduct_API_Success() throws Exception {
        Long productId = 1L;
        ProductDTO product = ProductDTO.builder()
                .id(productId)
                .name(new ProductName("나이키 운동화"))
                .description(new ProductDescription("나이키 에어맥스"))
                .price(new ProductPrice(BigDecimal.valueOf(100000)))
                .category(Category.SHOES)
                .brand("나이키")
                .build();



        when(productService.getProduct(eq(productId))).thenReturn(product);

        mockMvc.perform(get("/api/products/{id}", productId)
                        .param("role", "SELLER"))  // role 파라미터 추가
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("나이키 운동화"))  // 수정된 경로
                .andExpect(jsonPath("$.data.description").value("나이키 에어맥스"));

        verify(productService, times(1)).getProduct(productId);
    }

    @Test
    @DisplayName("상품 수정 API 성공")
    public void UpdateProduct_API_Success() throws Exception {
        Long productId = 1L;
        UpdateProductRequest updateRequest = new UpdateProductRequest(
                new ProductName("수정된 이름"),
                new ProductDescription("수정된 설명"),
                new ProductPrice(BigDecimal.valueOf(120000)),
                Category.SHOES,
                "나이키"
        );

        mockMvc.perform(put("/api/products/{id}", productId)
                        .param("role", "SELLER")  // role 파라미터 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"수정된 이름\", \"description\":\"수정된 설명\", \"price\":120000, \"category\":\"SHOES\", \"brand\":\"나이키\"}")
                )
                .andExpect(status().isOk());

        verify(productService, times(1)).updateProduct(eq(productId), any(UpdateProductRequest.class));
    }

    @Test
    @DisplayName("상품 삭제 API 성공")
    public void DeleteProduct_API_Success() throws Exception {
        Long productId = 1L;

        mockMvc.perform(patch("/api/products/{id}/disabled", productId)
                        .param("role", "SELLER")  // role 파라미터 추가
                )
                .andExpect(status().isOk());

        verify(productService, times(1)).disabled(productId);
    }


    @Test
    @DisplayName("리뷰 목록 조회 API 성공")
    public void GetReviewsByProductId_API_Success() throws Exception {
        Long productId = 1L;
        ReviewDTO review1 = ReviewDTO.builder()
                .userId(1L)
                .content(new ReviewContent("훌륭한 상품입니다."))
                .rating(new ReviewRating(5))
                .build();

        ReviewDTO review2 = ReviewDTO.builder()
                .userId(2L)
                .content(new ReviewContent("괜찮은 품질이네요."))
                .rating(new ReviewRating(4))
                .build();

        List<ReviewDTO> reviews = List.of(review1, review2);

        when(reviewService.getReviewsByProductId(productId)).thenReturn(reviews);

        mockMvc.perform(get("/api/products/{productId}/reviews", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].content").value("훌륭한 상품입니다."))
                .andExpect(jsonPath("$.data[0].rating").value(5))
                .andExpect(jsonPath("$.data[1].content").value("괜찮은 품질이네요."))
                .andExpect(jsonPath("$.data[1].rating").value(4));

        verify(reviewService, times(1)).getReviewsByProductId(productId);
    }

    @Test
    @DisplayName("리뷰 수정 API 성공")
    public void UpdateReview_API_Success() throws Exception {
        Long reviewId = 1L;
        Long userId = 1L;
        AddReviewRequest updateRequest = new AddReviewRequest(userId, new ReviewContent("리뷰 내용 수정 테스트입니다"), new ReviewRating(4));

        mockMvc.perform(put("/api/products/reviews/{reviewId}", reviewId)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1, \"content\":\"리뷰 내용 수정 테스트입니다\", \"rating\":4}")
                )
                .andExpect(status().isOk());

        verify(reviewService, times(1)).updateReview(eq(reviewId), any(AddReviewRequest.class), eq(userId));
    }

    @Test
    @DisplayName("리뷰 삭제 API 성공")
    public void DeleteReview_API_Success() throws Exception {
        Long reviewId = 1L;
        Long userId = 1L;

        mockMvc.perform(delete("/api/products/reviews/{reviewId}", reviewId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk());

        verify(reviewService, times(1)).deleteReview(eq(reviewId), eq(userId));
    }
}
