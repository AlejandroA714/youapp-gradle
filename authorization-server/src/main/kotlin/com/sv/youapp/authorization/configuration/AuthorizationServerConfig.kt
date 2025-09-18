package com.sv.youapp.authorization.configuration

import com.sv.youapp.authorization.repositories.ClientRepository
import com.sv.youapp.authorization.repositories.UserRepository
import com.sv.youapp.authorization.services.impl.DefaultNativeUserDetails
import com.sv.youapp.authorization.services.impl.JpaRegisteredClientRepository
import jakarta.persistence.EntityManagerFactory
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
@Profile("jdbc")
class AuthorizationServerConfig {
    @Bean
    fun dataSource(properties: DataSourceProperties): DataSource? {
        return properties.initializeDataSourceBuilder().build()
    }

    @Bean
    fun entityManagerFactory(
        dataSource: DataSource?,
        jpaProperties: JpaProperties,
    ): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = dataSource
        em.setPackagesToScan("com.sv.youapp")
        val vendorAdapter: JpaVendorAdapter = HibernateJpaVendorAdapter()
        em.jpaVendorAdapter = vendorAdapter
        val props = HashMap(jpaProperties.properties)
        props["hibernate.physical_naming_strategy"] = "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy"
        em.setJpaPropertyMap(props)
        return em
    }

    @Bean
    fun transactionManager(emf: EntityManagerFactory): PlatformTransactionManager {
        return JpaTransactionManager(emf)
    }

    @Bean
    fun userDetailsService(
        repository: UserRepository): UserDetailsService {
        return DefaultNativeUserDetails(repository)
    }

    @Bean
    fun registeredClientRepository(client: ClientRepository): RegisteredClientRepository {
        return JpaRegisteredClientRepository(client)
    }

    // TODO: MIGRATE REDIS
    @Bean
    fun authorizationService(): OAuth2AuthorizationService {
        return InMemoryOAuth2AuthorizationService()
    }
}
