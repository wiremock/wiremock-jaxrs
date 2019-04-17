package com.github.tomakehurst.wiremock.client;

import java.lang.reflect.Proxy;

public class JaxrsInvocationMappingBuilderFactory {

  public static <T> JaxrsInvocationMappingBuilder create(
      final Class<T> resource, final ResourceInvocation<T> invocation) {
    final JaxrsInvocationHandler handler = new JaxrsInvocationHandler();

    @SuppressWarnings("unchecked")
    final T recordingProxy =
        (T) Proxy.newProxyInstance(resource.getClassLoader(), new Class[] {resource}, handler);

    invocation.invoke(recordingProxy);

    return new JaxrsInvocationMappingBuilder(handler);
  }
}
