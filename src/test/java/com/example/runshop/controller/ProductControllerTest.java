package com.example.runshop.controller;

import com.example.runshop.model.dto.product.ProductDTO;
import com.example.runshop.model.dto.product.UpdateProductRequest;
import com.example.runshop.model.dto.product.AddProductRequest;
import com.example.runshop.model.enums.Category;
import com.example.runshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService)).build();
    }

    @Test
    public void 상품등록_API_성공() throws Exception {
        AddProductRequest request = new AddProductRequest(
                "나이키 운동화",
                "나이키 에어맥스",
                100000,
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
    public void 상품조회_API_성공() throws Exception {
        Long productId = 1L;
        ProductDTO product = ProductDTO.builder()
                .id(productId)
                .name("나이키 운동화")
                .description("나이키 에어맥스")
                .price(100000)
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
    public void 상품수정_API_성공() throws Exception {
        Long productId = 1L;
        UpdateProductRequest updateRequest = new UpdateProductRequest(
                "수정된 이름",
                "수정된 설명",
                120000,
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
    public void 상품삭제_API_성공() throws Exception {
        Long productId = 1L;

        mockMvc.perform(patch("/api/products/{id}/disabled", productId)
                        .param("role", "SELLER")  // role 파라미터 추가
                )
                .andExpect(status().isOk());

        verify(productService, times(1)).disabled(productId);
    }
}
