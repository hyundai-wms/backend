package com.myme.mywarehome.domains.user.adapter.out.persistence;

import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.domains.user.application.domain.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(Long userId);
    Optional<User> findById(String id);

    boolean existsById(String id);
    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u WHERE " +
            "(" +
            "   CASE WHEN (COALESCE(:name, '') != '' OR COALESCE(:id, '') != '' OR COALESCE(:phoneNumber, '') != '') " +
            "   THEN (" +
            "       (COALESCE(:name, '') != '' AND u.name LIKE CONCAT('%', :name, '%')) " +
            "       OR (COALESCE(:id, '') != '' AND u.id LIKE CONCAT('%', :id, '%')) " +
            "       OR (COALESCE(:phoneNumber, '') != '' AND u.phoneNumber LIKE CONCAT('%', :phoneNumber, '%'))" +
            "   ) " +
            "   ELSE true END" +
            ") " +
            "AND (:role IS NULL OR u.role = :role)")
    Page<User> findByConditions(
            @Param("name") String name,
            @Param("id") String id,
            @Param("phoneNumber") String phoneNumber,
            @Param("role") Role role,
            Pageable pageable
    );

    @Query("SELECT u FROM User u WHERE " +
            "u.role = :role")
    List<User> findAllByRole(Role role);
}
