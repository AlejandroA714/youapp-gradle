package com.sv.youapp.authorization.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "client")
class RegisteredClientEntity(
    @Id
    var id: String,
    @Column(nullable = false, unique = true)
    var clientId: String,
    @Column(nullable = false)
    var clientIdIssuedAt: Instant,
    @Column
    var clientSecret: String?,
    @Column
    var clientSecretExpiresAt: Instant?,
    @Column(nullable = false)
    var clientName: String,
    @OneToOne(mappedBy = "client")
    var clientSettings: SettingsEntity?,
    @OneToOne(mappedBy = "client")
    var tokenSettings: TokenSettingsEntity?,
) {
    @ManyToMany
    @JoinTable(
        name = "client_authentication_method",
        joinColumns = [JoinColumn(name = "client_id")],
        inverseJoinColumns = [JoinColumn(name = "method_id")],
    )
    lateinit var authenticationMethods: MutableSet<AuthenticationMethodEntity>

    @ManyToMany
    @JoinTable(
        name = "client_grant_type",
        joinColumns = [JoinColumn(name = "client_id")],
        inverseJoinColumns = [JoinColumn(name = "grant_type_id")],
    )
    lateinit var grantTypes: MutableSet<GrantTypeEntity>

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "client_id", nullable = false)
    lateinit var redirectUris: MutableSet<RedirectUriEntity>

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "client_id", nullable = false)
    lateinit var postLogoutRedirectUris: MutableSet<PostLogoutRedirectUriEntity>

    @ManyToMany
    @JoinTable(
        name = "client_scope",
        joinColumns = [JoinColumn(name = "client_id")],
        inverseJoinColumns = [JoinColumn(name = "scope_id")],
    )
    lateinit var scopes: MutableSet<ScopeEntity>
}
