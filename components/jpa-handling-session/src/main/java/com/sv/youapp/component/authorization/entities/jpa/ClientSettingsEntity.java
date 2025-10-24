package com.sv.youapp.component.authorization.entities.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "client_settings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ClientSettingsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne
	@JoinColumn(name = "client_id")
	private RegisteredClientEntity client;

	@Column(nullable = false)
	private Boolean requireProofKey = false;

	@Column(nullable = false)
	private Boolean requireAuthorizationConsent = false;
}
