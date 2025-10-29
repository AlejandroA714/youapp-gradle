package com.sv.youapp.bff.services.impl;

import com.sv.youapp.bff.dto.SessionRequest;
import com.sv.youapp.bff.services.SessionStorage;
import java.util.HashMap;
import java.util.Map;

public class InMemorySessionStorage implements SessionStorage {

  private final Map<String, SessionRequest> sessionMap = new HashMap<>();

  @Override
  public void save(String key, SessionRequest value) {
    sessionMap.put(key, value);
  }

  @Override
  public SessionRequest get(String key) {
    return sessionMap.get(key);
  }
}
