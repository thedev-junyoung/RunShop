package shoppingmall.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;

    // - **Product** 1 : 1 **Inventory**
    // (하나의 상품에 대해 하나의 재고가 관리됨)

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
