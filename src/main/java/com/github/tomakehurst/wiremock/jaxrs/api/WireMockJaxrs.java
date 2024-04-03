package com.github.tomakehurst.wiremock.jaxrs.api;

import com.github.tomakehurst.wiremock.client.JaxrsInvocationMappingBuilder;
import com.github.tomakehurst.wiremock.client.JaxrsInvocationMappingBuilderFactory;
import com.github.tomakehurst.wiremock.client.ResourceInvocation;

public class WireMockJaxrs {
  public static <T> JaxrsInvocationMappingBuilder invocation(
      final Class<T> resource, final ResourceInvocation<T> invocation) {
    return JaxrsInvocationMappingBuilderFactory.create(resource, invocation);
  }
}
