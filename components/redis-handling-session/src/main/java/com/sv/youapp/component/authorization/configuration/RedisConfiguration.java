package com.sv.youapp.component.authorization.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sv.youapp.common.authorization.dto.UserDTO;
import com.sv.youapp.component.authorization.converter.BytesToUsernamePasswordAuthenticationTokenConverter;
import com.sv.youapp.component.authorization.converter.UserDTOMixin;
import com.sv.youapp.component.authorization.converter.UsernamePasswordAuthenticationTokenToBytesConverter;
import com.sv.youapp.component.authorization.repositories.redis.OAuth2AuthorizationGrantAuthorizationRepository;
import com.sv.youapp.component.authorization.services.impl.RedisOAuth2AuthorizationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.convert.RedisCustomConversions;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

@Configuration
@EnableRedisRepositories(basePackages = { "com.sv.youapp" })
public class RedisConfiguration {

    @Bean
    public RedisCustomConversions redisCustomConversions() {
        ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.ALWAYS);
        objectMapper.registerModules(SecurityJackson2Modules.getModules(getClass().getClassLoader()));
        objectMapper.addMixIn(UserDTO.class, UserDTOMixin.class);
        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        objectMapper.configOverride(Map.class)
            .setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));
        objectMapper.configOverride(Collection.class)
            .setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));
        Jackson2JsonRedisSerializer<UsernamePasswordAuthenticationToken> serializer =
        new Jackson2JsonRedisSerializer<>(objectMapper, UsernamePasswordAuthenticationToken.class);
        return new RedisCustomConversions(
                Arrays.asList(
                    new UsernamePasswordAuthenticationTokenToBytesConverter(serializer),
                    new BytesToUsernamePasswordAuthenticationTokenConverter(serializer)
                )
                );
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(
    RegisteredClientRepository registeredClientRepository,
    OAuth2AuthorizationGrantAuthorizationRepository authorizationGrantAuthorizationRepository
    ) {
        return new RedisOAuth2AuthorizationService(
                registeredClientRepository,
        authorizationGrantAuthorizationRepository
        );
    }
}
