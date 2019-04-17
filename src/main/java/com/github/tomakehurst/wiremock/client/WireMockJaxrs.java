package com.github.tomakehurst.wiremock.client;

public class WireMockJaxrs {
  public static <T> JaxrsInvocationMappingBuilder invocation(
      final Class<T> resource, final ResourceInvocation<T> invocation) {
    return JaxrsInvocationMappingBuilderFactory.create(resource, invocation);
  }
}
