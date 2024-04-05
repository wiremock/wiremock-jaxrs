package com.github.tomakehurst.wiremock.jaxrs.api;

import com.github.tomakehurst.wiremock.client.JaxrsInvocationMappingBuilder;
import com.github.tomakehurst.wiremock.client.JaxrsInvocationMappingBuilderFactory;
import com.github.tomakehurst.wiremock.client.ResourceInvocation;

public class WireMockJaxrs {
  public static <T> JaxrsInvocationMappingBuilder invocation(
      final Class<T> resource, final ResourceInvocation<T> invocation) {
    return WireMockJaxrs.invocation(resource, invocation, MediaTypes.withMediaTypes());
  }

  public static <T> JaxrsInvocationMappingBuilder invocation(
      final Class<T> resource,
      final ResourceInvocation<T> invocation,
      final MediaTypes mediaTypes) {
    return JaxrsInvocationMappingBuilderFactory.create(resource, invocation, mediaTypes);
  }
}
