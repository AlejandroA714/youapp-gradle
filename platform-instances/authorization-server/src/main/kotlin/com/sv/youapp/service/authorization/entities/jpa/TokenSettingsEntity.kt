package com.sv.youapp.service.authorization.entities.jpa

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Converter
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.Duration

@Entity
@Table(name = "token_settings")
class TokenSettingsEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int,
    @OneToOne
    @JoinColumn(name = "client_id")
    var client: RegisteredClientEntity,
    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    @Convert(converter = DurationSecondsConverter::class)
    var accessTokenTtl: Duration,
    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    @Convert(converter = DurationSecondsConverter::class)
    var refreshTokenTtl: Duration,
    @Column(nullable = false)
    var reuseRefreshTokens: Boolean,
    @Column(nullable = false, length = 32)
    var accessTokenFormat: String,
) {
    @Converter
    class DurationSecondsConverter : AttributeConverter<Duration?, Int?> {
        override fun convertToDatabaseColumn(duration: Duration?): Int? {
            return (duration?.seconds?.toInt())
        }

        override fun convertToEntityAttribute(seconds: Int?): Duration? {
            return (if (seconds != null) Duration.ofSeconds(seconds.toLong()) else Duration.ofHours(16))
        }
    }
}
