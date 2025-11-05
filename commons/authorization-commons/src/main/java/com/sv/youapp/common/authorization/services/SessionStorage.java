package com.sv.youapp.common.authorization.services;

import com.sv.youapp.common.authorization.dto.SessionRequest;

public interface SessionStorage {

  void save(String key, SessionRequest value);

  SessionRequest get(String key);
}
