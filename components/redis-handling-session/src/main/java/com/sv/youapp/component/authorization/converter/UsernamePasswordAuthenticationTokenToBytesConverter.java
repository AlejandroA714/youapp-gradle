package com.sv.youapp.component.authorization.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@WritingConverter
public class UsernamePasswordAuthenticationTokenToBytesConverter
	implements Converter<UsernamePasswordAuthenticationToken, byte[]> {

	private final Jackson2JsonRedisSerializer<UsernamePasswordAuthenticationToken> serializer;

	public UsernamePasswordAuthenticationTokenToBytesConverter(
		Jackson2JsonRedisSerializer<UsernamePasswordAuthenticationToken> serializer
	) {
		this.serializer = serializer;
	}

	@Override
	public byte[] convert(UsernamePasswordAuthenticationToken value) {
		return this.serializer.serialize(value);
	}
}
