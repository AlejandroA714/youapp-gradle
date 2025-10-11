package com.sv.youapp.service.authorization.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

@WritingConverter
class UsernamePasswordAuthenticationTokenToBytesConverter(val serializer: Jackson2JsonRedisSerializer<UsernamePasswordAuthenticationToken?>) :
    Converter<UsernamePasswordAuthenticationToken?, ByteArray?> {
    override fun convert(value: UsernamePasswordAuthenticationToken): ByteArray {
        return this.serializer.serialize(value)
    }
}
