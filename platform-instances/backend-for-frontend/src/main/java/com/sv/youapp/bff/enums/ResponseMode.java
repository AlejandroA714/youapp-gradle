package com.sv.youapp.bff.enums;

public enum ResponseMode {
  QUERY,
  FORM_POST,
  FRAGMENT;

  @Override
  public String toString() {
    return name().toLowerCase(java.util.Locale.ROOT);
  }
}
