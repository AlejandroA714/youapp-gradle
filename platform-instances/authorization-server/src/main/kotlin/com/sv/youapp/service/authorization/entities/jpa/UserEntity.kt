package com.sv.youapp.service.authorization.entities.jpa

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
import java.time.Instant

@Entity
@Table(
    name = "users",
    indexes = [Index(columnList = "username", unique = true)],
)
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Integer,
    @Column(nullable = false, unique = true, length = 100)
    var username: String,
    @Column(nullable = false, length = 255)
    var password: String,
    @Column(nullable = false, unique = true, length = 255)
    var email: String,
    @Column(name = "profile_picture_url", length = 512)
    var profilePictureUrl: String?,
    @Column(nullable = false)
    var enabled: Boolean,
    @Column(name = "registered_at", nullable = false)
    var registeredAt: Instant,
    @ManyToMany
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")],
    )
    var roles: MutableSet<RoleEntity>,
    @ManyToMany
    @JoinTable(
        name = "user_authorities",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "authority_id")],
    )
    var authorities: MutableSet<AuthorityEntity>,
)
