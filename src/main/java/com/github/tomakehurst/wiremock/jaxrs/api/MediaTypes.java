package com.github.tomakehurst.wiremock.jaxrs.api;

public class MediaTypes {
  private String consumes;
  private String produces;

  public MediaTypes() {}

  public static MediaTypes withMediaTypes() {
    return new MediaTypes();
  }

  public MediaTypes consuming(final String consumes) {
    this.consumes = consumes;
    return this;
  }

  public MediaTypes producing(final String produces) {
    this.produces = produces;
    return this;
  }

  public String getConsumes() {
    return this.consumes;
  }

  public String getProduces() {
    return this.produces;
  }

  @Override
  public String toString() {
    return "MediaTypes [consumes=" + this.consumes + ", produces=" + this.produces + "]";
  }
}
