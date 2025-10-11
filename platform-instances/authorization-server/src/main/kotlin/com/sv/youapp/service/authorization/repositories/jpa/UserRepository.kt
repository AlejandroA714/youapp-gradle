package com.sv.youapp.service.authorization.repositories.jpa

import com.sv.youapp.service.authorization.entities.jpa.UserEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface UserRepository : JpaRepository<UserEntity, Integer> {
    @EntityGraph(attributePaths = ["roles", "roles.authorities", "authorities"])
    @Query(
        """
        SELECT u FROM UserEntity u
        LEFT JOIN FETCH u.roles r
        LEFT JOIN FETCH r.authorities
        LEFT JOIN FETCH u.authorities
        WHERE u.username = :username OR u.email = :username
    """,
    )
    fun findAllByUsername(username: String): Optional<UserEntity>
}
