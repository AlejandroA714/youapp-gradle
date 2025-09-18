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
    var id: Long,
    @OneToOne
    @JoinColumn(name = "client_id")
    var client: RegisteredClientEntity,
    @Column(nullable = false)
    var accessTokenTtl: Duration,
    @Column(nullable = false)
    var refreshTokenTtl: Duration,
    @Column(nullable = false)
    var reuseRefreshTokens: Boolean,
    @Column(nullable = false)
    var accessTokenFormat: String,
)
