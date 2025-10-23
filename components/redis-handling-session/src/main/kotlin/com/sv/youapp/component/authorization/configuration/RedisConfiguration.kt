package com.sv.youapp.component.authorization.configuration

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.sv.youapp.common.authorization.dto.UserDTO
import com.sv.youapp.component.authorization.converter.BytesToUsernamePasswordAuthenticationTokenConverter
import com.sv.youapp.component.authorization.converter.UserDTOMixin
import com.sv.youapp.component.authorization.converter.UsernamePasswordAuthenticationTokenToBytesConverter
import com.sv.youapp.component.authorization.repositories.redis.OAuth2AuthorizationGrantAuthorizationRepository
import com.sv.youapp.component.authorization.services.impl.RedisOAuth2AuthorizationService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.convert.RedisCustomConversions
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.jackson2.SecurityJackson2Modules
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module

@Configuration
@EnableRedisRepositories(basePackages = ["com.sv.youapp"])
class RedisConfiguration {
    @Bean
    fun redisCustomConversions(): RedisCustomConversions {
        val objectMapper =
            ObjectMapper()
                .registerKotlinModule()
                .setSerializationInclusion(JsonInclude.Include.ALWAYS)
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
