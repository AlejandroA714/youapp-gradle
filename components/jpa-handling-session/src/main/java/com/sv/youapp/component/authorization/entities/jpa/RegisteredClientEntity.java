package com.sv.youapp.component.authorization.entities.jpa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "client")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RegisteredClientEntity {

	@Id
	@Column(length = 100, nullable = false, updatable = false)
	private String id;

	@Column(name = "client_id", nullable = false, unique = true, length = 100)
	private String clientId;

	@Column(name = "client_secret", length = 200)
	private String clientSecret;

	@Column(name = "client_name", length = 200)
	private String clientName;

	@Column(name = "client_id_issued_at")
	private Instant clientIdIssuedAt;

	@Column(name = "client_secret_expires_at")
	private Instant clientSecretExpiresAt;

	@OneToOne(mappedBy = "client")
	private ClientSettingsEntity clientSettings;

	@OneToOne(mappedBy = "client")
	private TokenSettingsEntity tokenSettings;

	@ManyToMany
	@JoinTable(
		name = "client_authentication_method",
		joinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(name = "method_id", referencedColumnName = "id")
	)
	private Set<AuthenticationMethodEntity> authenticationMethods;

	@ManyToMany
	@JoinTable(
		name = "client_grant_type",
		joinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(name = "grant_type_id", referencedColumnName = "id")
	)
	private Set<GrantTypeEntity> grantTypes;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "client_id", nullable = false)
	private Set<RedirectUriEntity> redirectUris;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "client_id", nullable = false)
	private Set<PostLogoutRedirectUriEntity> postLogoutRedirectUris;

	@ManyToMany
	@JoinTable(
		name = "client_scope",
		joinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(name = "scope_id", referencedColumnName = "id")
	)
	private Set<ScopeEntity> scopes;
}
