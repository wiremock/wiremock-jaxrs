package com.github.tomakehurst.wiremock.client;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import javax.ws.rs.core.HttpHeaders;

public class JaxrsInvocationMappingBuilder extends BasicMappingBuilder {

  private final JaxrsInvocationHandler handler;

  public JaxrsInvocationMappingBuilder(final JaxrsInvocationHandler handler) {
    super(handler.getRequestMethod(), new UrlPattern(new RegexPattern(get(handler)), true));
    this.handler = handler;

    if (!handler.getRequestContentTypeList().isEmpty()) {
      this.withHeader(
          HttpHeaders.CONTENT_TYPE, new EqualToPattern(handler.getRequestContentTypeList().get(0)));
    }

    if (!handler.getResponseContentTypeList().isEmpty()) {
      this.withHeader(
          HttpHeaders.ACCEPT, new EqualToPattern(handler.getResponseContentTypeList().get(0)));
    }

    if (handler.findPostObject().isPresent()) {
      final Boolean ignoreArrayOrder = true;
      final Boolean ignoreExtraElements = true;
      final String json = toJson(handler.findPostObject().get());
      final EqualToJsonPattern bodyPattern =
          new EqualToJsonPattern(json, ignoreArrayOrder, ignoreExtraElements);
      this.withRequestBody(bodyPattern);
    }

    for (final InvocationParam qp : handler.getQueryParams()) {
      final Object value = qp.getValue();
      if (value == null) {
        continue;
      }
      final StringValuePattern valuePattern = getStringValuePattern(value);
      this.withQueryParam(qp.getName(), valuePattern);
    }
  }

  private static String get(final JaxrsInvocationHandler handler) {
    return ".*" + handler.getPath() + (handler.getQueryParams().isEmpty() ? "$" : "\\?.*");
  }

  public BasicMappingBuilder willReturn(
      final ResponseDefinitionBuilder responseDefBuilder, final Object responseObject) {
    final Class<?> expectedReturnType = handler.getReturnType();
    if (responseDefBuilder.status >= 200 && responseDefBuilder.status <= 299) {
      if (!expectedReturnType.isAssignableFrom(responseObject.getClass())) {
        throw new RuntimeException(
            "Cannot assign " + expectedReturnType + " from " + responseObject.getClass());
      }
    }

    responseDefBuilder.withBody(toJson(responseObject));

    decorateOnWillReturn(responseDefBuilder);

    return super.willReturn(responseDefBuilder);
  }

  @Override
  public BasicMappingBuilder willReturn(final ResponseDefinitionBuilder responseDefBuilder) {
    final Class<?> expectedReturnType = handler.getReturnType();
    if (responseDefBuilder.status >= 200 && responseDefBuilder.status <= 299) {
      if (!expectedReturnType.getName().equals("void")) {
        throw new RuntimeException(
            "Stubbed method returns "
                + expectedReturnType
                + ", use the method willReturn(responseDefBuilder, responseObject)");
      }
    }

    decorateOnWillReturn(responseDefBuilder);

    return super.willReturn(responseDefBuilder);
  }

  private void decorateOnWillReturn(final ResponseDefinitionBuilder responseDefBuilder) {
    if (!handler.getResponseContentTypeList().isEmpty()) {
      responseDefBuilder.withHeader(
          HttpHeaders.CONTENT_TYPE, handler.getResponseContentTypeList().get(0));
    }
  }

  private StringValuePattern getStringValuePattern(final Object value) {
    StringValuePattern valuePattern = null;
    final Boolean ignoreArrayOrder = true;
    final Boolean ignoreExtraElements = true;
    if (value.getClass() != Object.class
        && (value.getClass().isPrimitive()
            || value.getClass().getName().startsWith("java.lang."))) {
      valuePattern = new EqualToPattern(value.toString());
    } else {
      final String json = toJson(value);
      valuePattern = new EqualToJsonPattern(json, ignoreArrayOrder, ignoreExtraElements);
    }
    return valuePattern;
  }

  private static String toJson(final Object object) {
    try {
      return new ObjectMapper() //
          .setSerializationInclusion(Include.NON_EMPTY) //
          .writerWithDefaultPrettyPrinter()
          .writeValueAsString(object);
    } catch (final JsonProcessingException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
