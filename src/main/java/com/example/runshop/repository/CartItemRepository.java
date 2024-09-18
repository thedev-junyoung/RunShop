package com.example.runshop.repository;

import com.example.runshop.model.entity.CartItem;
import com.example.runshop.model.entity.Product;
import com.example.runshop.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByUserAndProduct(User user, Product product);

    Page<CartItem> findByUser(User user, Pageable pageable);
}
