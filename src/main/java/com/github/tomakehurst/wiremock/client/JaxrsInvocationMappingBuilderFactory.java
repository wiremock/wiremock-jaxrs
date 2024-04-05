package com.github.tomakehurst.wiremock.client;

import com.github.tomakehurst.wiremock.jaxrs.api.MediaTypes;
import java.lang.reflect.Proxy;

public class JaxrsInvocationMappingBuilderFactory {

  public static <T> JaxrsInvocationMappingBuilder create(
      final Class<T> resource,
      final ResourceInvocation<T> invocation,
      final MediaTypes mediaTypes) {
    final JaxrsInvocationHandler handler = new JaxrsInvocationHandler();

    final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    @SuppressWarnings("unchecked")
    final T recordingProxy =
        (T) Proxy.newProxyInstance(contextClassLoader, new Class[] {resource}, handler);

    invocation.invoke(recordingProxy);

    return new JaxrsInvocationMappingBuilder(handler, mediaTypes);
  }
}
