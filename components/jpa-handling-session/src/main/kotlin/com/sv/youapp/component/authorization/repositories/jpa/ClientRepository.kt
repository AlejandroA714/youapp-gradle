package com.sv.youapp.component.authorization.repositories.jpa

import com.sv.youapp.component.authorization.entities.jpa.RegisteredClientEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
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
