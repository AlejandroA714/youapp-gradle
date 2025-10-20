package com.sv.youapp.component.authorization.entities.jpa

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(
    name = "roles",
    indexes = [Index(columnList = "name", unique = true)],
)
class RoleEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int,
    @Column(nullable = false, unique = true, length = 100)
    var name: String,
    @Column(length = 255)
    var description: String?,
    @ManyToMany
    @JoinTable(
        name = "role_authorities",
        joinColumns = [JoinColumn(name = "role_id")],
        inverseJoinColumns = [JoinColumn(name = "authority_id")],
    )
    var authorities: MutableSet<AuthorityEntity>,
)
