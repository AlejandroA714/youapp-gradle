package com.sv.youapp.bff.enums;

public enum ResponseType {
  CODE,
  TOKEN,
  ID_TOKEN;

  @Override
  public String toString() {
    return name().toLowerCase(java.util.Locale.ROOT);
  }
}
