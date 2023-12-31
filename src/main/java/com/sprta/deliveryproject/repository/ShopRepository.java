package com.sprta.deliveryproject.repository;

import com.sprta.deliveryproject.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    List<Shop> findAllByCategory_IdOrderByModifiedAtDesc(Long category_id);

    Optional<Shop> findByUsername(String username);
}
