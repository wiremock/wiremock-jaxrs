package com.github.tomakehurst.wiremock.client;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.tomakehurst.wiremock.matching.ContentPattern;
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.matching.EqualToXmlPattern;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import jakarta.ws.rs.core.HttpHeaders;

public class JaxrsInvocationMappingBuilder extends BasicMappingBuilder {

  private final JaxrsInvocationHandler handler;

  public JaxrsInvocationMappingBuilder(final JaxrsInvocationHandler handler) {
    super(handler.getRequestMethod(), new UrlPattern(new RegexPattern(get(handler)), true));
    this.handler = handler;

    final String requestBodyContentType = this.getRequestBodyContentType(handler);
    if (requestBodyContentType != null) {
      this.withHeader(HttpHeaders.CONTENT_TYPE, new EqualToPattern(requestBodyContentType));
    }

    if (!handler.getResponseContentTypeList().isEmpty()) {
      this.withHeader(
          HttpHeaders.ACCEPT, new EqualToPattern(handler.getResponseContentTypeList().get(0)));
    }

    final ContentPattern<String> requestBodyContentPattern =
        this.createRequestBodyContentPattern(handler);
    if (requestBodyContentPattern != null) {
      this.withRequestBody(requestBodyContentPattern);
    }

    for (final InvocationParam qp : handler.getQueryParams()) {
      final Object value = qp.getValue();
      if (value == null) {
        continue;
      }
      final StringValuePattern valuePattern = this.getStringValuePattern(value);
      this.withQueryParam(qp.getName(), valuePattern);
    }
  }

  private ContentPattern<String> createRequestBodyContentPattern(
      final JaxrsInvocationHandler handler) {
    if (!handler.findPostObject().isPresent()) {
      return null;
    }

    final Object requestBody = handler.findPostObject().get();
    final String requestBodyContentType = this.getRequestBodyContentType(handler);
    if (requestBodyContentType == null) {
      if (!(requestBody instanceof String)) {
        throw new IllegalArgumentException(
            "Cannot serialize request body as Content-Type is not defined");
      }

      return new EqualToPattern((String) requestBody);
    }

    switch (requestBodyContentType) {
      case "application/json":
        {
          final Boolean ignoreArrayOrder = true;
          final Boolean ignoreExtraElements = true;
          final String json = toJson(requestBody);

          return new EqualToJsonPattern(json, ignoreArrayOrder, ignoreExtraElements);
        }
      case "application/xml":
        {
          final String xml = toXml(requestBody);

          return new EqualToXmlPattern(xml);
        }
      default:
        throw new IllegalArgumentException(
            "Content-Type " + requestBodyContentType + " is not supported");
    }
  }

  private String getRequestBodyContentType(final JaxrsInvocationHandler handler) {
    if (handler.getRequestContentTypeList().isEmpty()) {
      return null;
    }

    return handler.getRequestContentTypeList().get(0);
  }

  private static String get(final JaxrsInvocationHandler handler) {
    return ".*" + handler.getPath() + (handler.getQueryParams().isEmpty() ? "$" : "\\?.*");
  }

  public BasicMappingBuilder willReturn(
      final ResponseDefinitionBuilder responseDefBuilder, final Object responseObject) {
    final Class<?> expectedReturnType = this.handler.getReturnType();
    if (responseDefBuilder.status >= 200 && responseDefBuilder.status <= 299) {
      if (!expectedReturnType.isAssignableFrom(responseObject.getClass())) {
        throw new RuntimeException(
            "Cannot assign " + expectedReturnType + " from " + responseObject.getClass());
      }
    }

    responseDefBuilder.withBody(toJson(responseObject));

    this.decorateOnWillReturn(responseDefBuilder);

    return super.willReturn(responseDefBuilder);
  }

  @Override
  public BasicMappingBuilder willReturn(final ResponseDefinitionBuilder responseDefBuilder) {
    final Class<?> expectedReturnType = this.handler.getReturnType();
    if (responseDefBuilder.status >= 200 && responseDefBuilder.status <= 299) {
      if (!expectedReturnType.getName().equals("void")) {
        throw new RuntimeException(
            "Stubbed method returns "
                + expectedReturnType
                + ", use the method willReturn(responseDefBuilder, responseObject)");
      }
    }

    this.decorateOnWillReturn(responseDefBuilder);

    return super.willReturn(responseDefBuilder);
  }

  private void decorateOnWillReturn(final ResponseDefinitionBuilder responseDefBuilder) {
    if (!this.handler.getResponseContentTypeList().isEmpty()) {
      responseDefBuilder.withHeader(
          HttpHeaders.CONTENT_TYPE, this.handler.getResponseContentTypeList().get(0));
    }
  }

  private StringValuePattern getStringValuePattern(final Object value) {
    final Boolean ignoreArrayOrder = true;
    final Boolean ignoreExtraElements = true;
    if (value.getClass() != Object.class
        && (value.getClass().isPrimitive()
            || value.getClass().getName().startsWith("java.lang."))) {
      return new EqualToPattern(value.toString());
    } else {
      final String json = toJson(value);
      return new EqualToJsonPattern(json, ignoreArrayOrder, ignoreExtraElements);
    }
  }

  private static String toJson(final Object object) {
    return serializeWithObjectMapper(object, new ObjectMapper());
  }

  private static String toXml(final Object object) {
    return serializeWithObjectMapper(object, new XmlMapper());
  }

  private static String serializeWithObjectMapper(
      final Object object, final ObjectMapper objectMapper) {
    try {
      if (object instanceof String) {
        return (String) object;
      }

      return objectMapper //
          .setSerializationInclusion(Include.NON_EMPTY) //
          .writerWithDefaultPrettyPrinter()
          .writeValueAsString(object);
    } catch (final JsonProcessingException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
