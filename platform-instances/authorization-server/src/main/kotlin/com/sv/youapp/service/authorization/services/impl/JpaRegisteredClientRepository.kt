package com.sv.youapp.service.authorization.services.impl

import com.sv.youapp.service.authorization.mapper.toRegisteredClient
import com.sv.youapp.service.authorization.repositories.jpa.ClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository

class JpaRegisteredClientRepository
(private val clientRepository: ClientRepository) : RegisteredClientRepository {
    override fun save(registeredClient: RegisteredClient?) {
        // TODO("NOT IMPLEMENTED")
    }

    override fun findById(id: String?): RegisteredClient? {
        if (id.isNullOrBlank()) return null
        return clientRepository.findById(id)
            .map { it.toRegisteredClient() }
            .orElse(null)
    }

    override fun findByClientId(clientId: String?): RegisteredClient? {
        if (clientId.isNullOrBlank()) return null
        return clientRepository.findByClientId(clientId)
            .map { it.toRegisteredClient() }
            .orElse(null)
    }
}
