package com.hospital_management.hospitalmanagement.auth.repo;

import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import com.hospital_management.hospitalmanagement.auth.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PasswordTokenRepo extends JpaRepository<PasswordResetToken, Long> {

    @Query("""
         select t from PasswordResetToken t
         where t.token = :token
         and t.expiresAt > CURRENT_TIMESTAMP
    """)
    Optional<PasswordResetToken> findValidToken(@Param("token") String token);

    @Modifying
    @Transactional
    @Query("delete from PasswordResetToken t where t.appUser = :appUser")
    void deleteByUser(@Param("appUser") AppUser appUser);
}
