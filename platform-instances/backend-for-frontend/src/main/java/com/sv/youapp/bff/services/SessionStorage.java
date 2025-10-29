package com.sv.youapp.bff.services;

import com.sv.youapp.bff.dto.SessionRequest;

public interface SessionStorage {

	void save(String key, SessionRequest value);

	SessionRequest get(String key);
}
