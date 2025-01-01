package com.plenamente.sgt.infra.repository;

import com.plenamente.sgt.domain.entity.Rol;
import com.plenamente.sgt.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findByRol(Rol role);

    @Query("SELECT u FROM User u WHERE u.isTherapist = true")
    List<User> findByIsTherapistTrue();

    boolean existsByDni(String dni);

    boolean existsByEmail(String email);

}
