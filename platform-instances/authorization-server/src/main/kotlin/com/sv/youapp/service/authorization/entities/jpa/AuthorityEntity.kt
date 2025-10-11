package com.sv.youapp.service.authorization.entities.jpa

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "authorities",
    indexes = [Index(columnList = "name", unique = true)],
)
class AuthorityEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int,
    @Column(nullable = false, unique = true, length = 100)
    var name: String,
    @Column(length = 255)
    var description: String? = null,
)
