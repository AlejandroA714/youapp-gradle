package com.sv.youapp.authorization.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest

@ReadingConverter
class BytesToOAuth2AuthorizationRequestConverter(private val serializer: Jackson2JsonRedisSerializer<OAuth2AuthorizationRequest?>) :
    Converter<ByteArray?, OAuth2AuthorizationRequest?> {
    public override fun convert(value: ByteArray): OAuth2AuthorizationRequest? {
        return this.serializer.deserialize(value)
    }
}
