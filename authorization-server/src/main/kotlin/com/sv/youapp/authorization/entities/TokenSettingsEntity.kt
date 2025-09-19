package com.sv.youapp.authorization.entities

import jakarta.persistence.Column
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
    var accessTokenTtl: Duration,
    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    var refreshTokenTtl: Duration,
    @Column(nullable = false)
    var reuseRefreshTokens: Boolean,
    @Column(nullable = false, length = 32)
    var accessTokenFormat: String,
)
