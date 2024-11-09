package com.example.runshop.model.entity;

import com.example.runshop.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Seller extends User {

    @Column(name = "business_name", nullable = false)
    private String businessName;  // 사업자명

    @Column(name = "business_registration_number", nullable = false, unique = true)
    private String businessRegistrationNumber;  // 사업자 등록번호

    @Column(name = "approved")
    private boolean approved = false;  // 판매자 승인 여부

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();  // 판매자가 등록한 상품 목록

    // 판매자 관련 추가 정보가 필요할 경우 이곳에 필드를 추가할 수 있음
    public Seller() {
        super();
        this.updateRole(UserRole.SELLER);
    }

}
