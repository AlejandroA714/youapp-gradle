package com.sv.youapp.component.authorization.entities.jpa;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;

@Entity
@Table(name = "token_settings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TokenSettingsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne
	@JoinColumn(name = "client_id")
	private RegisteredClientEntity client;

	@Column(name = "access_token_ttl")
	@Convert(converter = DurationSecondsConverter.class)
	private Duration accessTokenTimeToLive;

	@Column(name = "refresh_token_ttl")
	@Convert(converter = DurationSecondsConverter.class)
	private Duration refreshTokenTimeToLive;

	@Column(nullable = false, length = 32)
	private String accessTokenFormat;

	@Converter
	public static class DurationSecondsConverter implements AttributeConverter<Duration, Integer> {
		@Override
		public Integer convertToDatabaseColumn(Duration duration) {
			return duration == null ? null : Math.toIntExact(duration.getSeconds());
		}
		@Override
		public Duration convertToEntityAttribute(Integer seconds) {
			return seconds == null ? Duration.ofHours(16) : Duration.ofSeconds(seconds.longValue());
		}
	}
}
