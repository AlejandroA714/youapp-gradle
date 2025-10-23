package com.sv.youapp.component.authorization.configuration

import com.sv.youapp.common.authorization.services.NativeUserDetails
import com.sv.youapp.component.authorization.repositories.jpa.ClientRepository
import com.sv.youapp.component.authorization.repositories.jpa.UserRepository
import com.sv.youapp.component.authorization.services.impl.JpaNativeUserDetails
import com.sv.youapp.component.authorization.services.impl.JpaRegisteredClientRepository
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository

@Configuration
@EnableJpaRepositories(basePackages = ["com.sv.youapp"])
@EntityScan(basePackages = ["com.sv.youapp"])
class RemoteConfiguration {
    @Bean
    fun jpaUserDetails(userRepository: UserRepository): NativeUserDetails {
        return JpaNativeUserDetails(userRepository)
    }

    @Bean
    fun jpaRegisteredClientRepository(clientRepository: ClientRepository): RegisteredClientRepository {
        return JpaRegisteredClientRepository(clientRepository)
    }
}
