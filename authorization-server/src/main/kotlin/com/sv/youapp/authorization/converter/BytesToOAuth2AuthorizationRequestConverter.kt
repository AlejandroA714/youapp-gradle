package com.sv.youapp.authorization.converter

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.security.jackson2.SecurityJackson2Modules
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module


@ReadingConverter
class BytesToOAuth2AuthorizationRequestConverter(private val serializer: Jackson2JsonRedisSerializer<OAuth2AuthorizationRequest?>) :
    Converter<ByteArray?, OAuth2AuthorizationRequest?> {

    public override fun convert(value: ByteArray): OAuth2AuthorizationRequest? {
        return this.serializer.deserialize(value)
    }
}
