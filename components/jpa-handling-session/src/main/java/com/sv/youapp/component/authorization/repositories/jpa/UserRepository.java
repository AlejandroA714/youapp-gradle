package com.sv.youapp.component.authorization.repositories.jpa;

import com.sv.youapp.component.authorization.entities.jpa.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    @EntityGraph(attributePaths = { "roles", "authorities" })
    Optional<UserEntity> findByUsername(String username);

   // Optional<UserEntity> findByUsername(String username);
}
