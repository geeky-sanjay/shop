package com.onlineshop.shop.authentication.repositories;

import com.onlineshop.shop.authentication.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByValueAndIsDeletedEquals(String token, boolean isDeleted);
   // Optional<Token> findByValueAndIsDeletedEqualsAndExpiryDateGreaterThen(String value,boolean isDeleted, Date expiryDate);
}
