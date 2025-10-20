package com.sv.youapp.component.authorization.entities.jpa

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "grant_type")
class GrantTypeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int,
    @Column(nullable = false, unique = true)
    var name: String,
) {
    @ManyToMany(mappedBy = "grantTypes")
    lateinit var clients: MutableSet<RegisteredClientEntity>
}
