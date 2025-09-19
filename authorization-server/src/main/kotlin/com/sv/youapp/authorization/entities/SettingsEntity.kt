package com.sv.youapp.authorization.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "client_settings")
class SettingsEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int,
    @OneToOne
    @JoinColumn(name = "client_id")
    var client: RegisteredClientEntity,
    @Column(nullable = false)
    var requireProofKey: Boolean = false,
    @Column(nullable = false)
    var requireAuthorizationConsent: Boolean = false,
)
