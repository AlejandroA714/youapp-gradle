package com.sv.youapp.component.authorization.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@ReadingConverter
public class BytesToUsernamePasswordAuthenticationTokenConverter
implements Converter<byte[], UsernamePasswordAuthenticationToken> {

    private final Jackson2JsonRedisSerializer<UsernamePasswordAuthenticationToken> serializer;

    public BytesToUsernamePasswordAuthenticationTokenConverter(
            Jackson2JsonRedisSerializer<UsernamePasswordAuthenticationToken> serializer
            ) {
        this.serializer = serializer;
    }

    @Override
    public UsernamePasswordAuthenticationToken convert(byte[] value) {
        return this.serializer.deserialize(value);
    }
}
