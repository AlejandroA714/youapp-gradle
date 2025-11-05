package com.sv.youapp.bff.services.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.sv.youapp.common.authorization.dto.SessionRequest;
import com.sv.youapp.common.authorization.services.SessionStorage;
import lombok.NonNull;

public record InMemorySessionStorage(@NonNull Cache<String, SessionRequest> cache)
    implements SessionStorage {

  @Override
  public void save(String key, SessionRequest value) {
    cache.put(key, value);
  }

  @Override
  public SessionRequest get(String key) {
    return cache.getIfPresent(key);
  }
}
