package com.sv.youapp.authorization.configuration

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.sv.youapp.authorization.converter.BytesToUsernamePasswordAuthenticationTokenConverter
import com.sv.youapp.authorization.converter.UserDTOMixin
import com.sv.youapp.authorization.converter.UsernamePasswordAuthenticationTokenToBytesConverter
import com.sv.youapp.authorization.dto.UserDTO
import com.sv.youapp.authorization.repositories.jpa.ClientRepository
import com.sv.youapp.authorization.repositories.jpa.UserRepository
import com.sv.youapp.authorization.repositories.redis.OAuth2AuthorizationGrantAuthorizationRepository
import com.sv.youapp.authorization.services.impl.DefaultNativeUserDetails
import com.sv.youapp.authorization.services.impl.JpaRegisteredClientRepository
import com.sv.youapp.authorization.services.impl.RedisOAuth2AuthorizationService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.convert.RedisCustomConversions
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.jackson2.SecurityJackson2Modules
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module

@Configuration
@Profile("jdbc")
@EnableRedisRepositories("com.sv.youapp.authorization.repositories.redis")
@EnableJpaRepositories("com.sv.youapp.authorization.repositories.jpa")
class AuthorizationServerConfig {
    @Bean
    fun userDetailsService(repository: UserRepository): UserDetailsService {
        return DefaultNativeUserDetails(repository)
    }

    @Bean
    fun registeredClientRepository(client: ClientRepository): RegisteredClientRepository {
        return JpaRegisteredClientRepository(client)
    }

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return JedisConnectionFactory()
    }

    @Bean
    fun redisTemplate(
        connectionFactory: RedisConnectionFactory,
        objectMapper: ObjectMapper,
    ): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory

        val serializer = Jackson2JsonRedisSerializer(objectMapper, Any::class.java)
        template.valueSerializer = serializer
        template.hashValueSerializer = serializer
        template.keySerializer = StringRedisSerializer()
        template.hashKeySerializer = StringRedisSerializer()

        template.afterPropertiesSet()
        return template
    }

//    @Bean
//    fun objectMapper(): ObjectMapper {
//
//        return objectMapper
//    }

    @Bean
    fun redisCustomConversions(): RedisCustomConversions {
        val objectMapper =
            ObjectMapper()
                .registerKotlinModule()
                .setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
        objectMapper.registerModules(SecurityJackson2Modules.getModules(javaClass.getClassLoader()))
        objectMapper.addMixIn(UserDTO::class.java, UserDTOMixin::class.java)
        objectMapper.registerModule(OAuth2AuthorizationServerJackson2Module())
        objectMapper.configOverride(Map::class.java)
            .setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY))
        objectMapper.configOverride(MutableCollection::class.java)
            .setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY))
        val serializer =
            Jackson2JsonRedisSerializer(objectMapper, UsernamePasswordAuthenticationToken::class.java)
        return RedisCustomConversions(
            listOf<Any?>(
                UsernamePasswordAuthenticationTokenToBytesConverter(serializer),
                BytesToUsernamePasswordAuthenticationTokenConverter(serializer),
            ),
        )
    }

    @Bean
    fun authorizationService(
        registeredClientRepository: RegisteredClientRepository,
        authorizationGrantAuthorizationRepository: OAuth2AuthorizationGrantAuthorizationRepository,
    ): OAuth2AuthorizationService {
        return RedisOAuth2AuthorizationService(registeredClientRepository, authorizationGrantAuthorizationRepository)
    }
}
