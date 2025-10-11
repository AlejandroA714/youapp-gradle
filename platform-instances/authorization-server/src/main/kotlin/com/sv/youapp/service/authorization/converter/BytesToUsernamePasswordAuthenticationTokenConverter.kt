package com.sv.youapp.service.authorization.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

@ReadingConverter
class BytesToUsernamePasswordAuthenticationTokenConverter(val serializer: Jackson2JsonRedisSerializer<UsernamePasswordAuthenticationToken?>) :
    Converter<ByteArray?, UsernamePasswordAuthenticationToken?> {
    override fun convert(value: ByteArray): UsernamePasswordAuthenticationToken? {
        return this.serializer.deserialize(value)
    }
}
