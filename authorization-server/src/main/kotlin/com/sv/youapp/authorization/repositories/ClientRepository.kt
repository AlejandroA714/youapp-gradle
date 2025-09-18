package com.sv.youapp.authorization.repositories

import com.sv.youapp.authorization.entities.RegisteredClientEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ClientRepository : JpaRepository<RegisteredClientEntity, String> {
    @EntityGraph(
        attributePaths = [
            "authenticationMethods",
            "grantTypes",
            "scopes",
            "redirectUris",
            "postLogoutRedirectUris",
            "clientSettings",
            "tokenSettings",
        ],
    )
    fun findByClientId(clientId: String): Optional<RegisteredClientEntity>

    @EntityGraph(
        attributePaths = [
            "authenticationMethods",
            "grantTypes",
            "scopes",
            "redirectUris",
            "postLogoutRedirectUris",
            "clientSettings",
            "tokenSettings",
        ],
    )
    override fun findById(id: String): Optional<RegisteredClientEntity>
}
