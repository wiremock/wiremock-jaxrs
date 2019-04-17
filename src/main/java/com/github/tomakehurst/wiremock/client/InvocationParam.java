package com.github.tomakehurst.wiremock.client;

public class InvocationParam {
  private final String name;
  private final Object value;

  public InvocationParam(final String name, final Object value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public Object getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "QueryParams [name=" + name + ", value=" + value + "]";
  }
}
