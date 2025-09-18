package com.sv.youapp.authorization.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "authentication_method")
class AuthenticationMethodEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,
    @Column(nullable = false, unique = true)
    var name: String,
) {
    @ManyToMany(mappedBy = "authenticationMethods")
    lateinit var clients: MutableSet<RegisteredClientEntity>
}
