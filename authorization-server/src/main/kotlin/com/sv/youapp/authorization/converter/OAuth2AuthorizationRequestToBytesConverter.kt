package com.sv.youapp.authorization.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest


@WritingConverter
class OAuth2AuthorizationRequestToBytesConverter(private val serializer: Jackson2JsonRedisSerializer<OAuth2AuthorizationRequest?>)
    : Converter<OAuth2AuthorizationRequest?, ByteArray?> {

    public override fun convert(value: OAuth2AuthorizationRequest): ByteArray {
        return this.serializer.serialize(value)
    }
}
