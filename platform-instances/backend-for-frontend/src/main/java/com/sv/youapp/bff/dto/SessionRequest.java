package com.sv.youapp.bff.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

@Data
@AllArgsConstructor
public class SessionRequest {
	private String state;
	private String scope;
	private String codeVerifier;
	private String nonce;
}
