package com.sv.youapp.component.authorization.repositories.jpa;

import com.sv.youapp.component.authorization.entities.jpa.RegisteredClientEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<RegisteredClientEntity, String> {

    @EntityGraph(attributePaths = {
        "authenticationMethods",
        "grantTypes",
        "scopes",
        "redirectUris",
        "postLogoutRedirectUris",
        "clientSettings",
        "tokenSettings"
    })
    Optional<RegisteredClientEntity> findByClientId(String clientId);

	@EntityGraph(attributePaths = {
        "authenticationMethods",
        "grantTypes",
        "scopes",
        "redirectUris",
        "postLogoutRedirectUris",
        "clientSettings",
        "tokenSettings"
    })
    Optional<RegisteredClientEntity> findById(String id);
}
