package com.github.tomakehurst.wiremock.client;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.tomakehurst.wiremock.jaxrs.api.MediaTypes;
import com.github.tomakehurst.wiremock.matching.ContentPattern;
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.matching.EqualToXmlPattern;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

public class JaxrsInvocationMappingBuilder extends BasicMappingBuilder {

  private final JaxrsInvocationHandler handler;
  private final MediaTypes mediaTypes;

  public JaxrsInvocationMappingBuilder(
      final JaxrsInvocationHandler handler, final MediaTypes mediaTypes) {
    super(handler.getRequestMethod(), new UrlPattern(new RegexPattern(get(handler)), true));
    this.handler = handler;
    this.mediaTypes = mediaTypes;
    final String requestBodyContentType =
        this.getContentType(handler.getRequestContentTypeList(), mediaTypes.getConsumes());
    if (requestBodyContentType != null) {
      this.withHeader(HttpHeaders.CONTENT_TYPE, new EqualToPattern(requestBodyContentType));
    }

    final ContentPattern<String> requestBodyContent =
        this.createRequestBodyContentPattern(handler, mediaTypes.getConsumes());
    if (requestBodyContent != null) {
      this.withRequestBody(requestBodyContent);
    }

    final String responseBodyContentType =
        this.getContentType(handler.getResponseContentTypeList(), mediaTypes.getProduces());
    if (responseBodyContentType != null) {
      this.withHeader(HttpHeaders.ACCEPT, new EqualToPattern(responseBodyContentType));
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
      final JaxrsInvocationHandler handler, final String mediaType) {
    if (!handler.findPostObject().isPresent()) {
      return null;
    }

    final Object body = handler.findPostObject().get();
    final String requestBodyContentType =
        this.getContentType(handler.getRequestContentTypeList(), mediaType);
    if (requestBodyContentType == null) {
      if (!(body instanceof String)) {
        throw new IllegalArgumentException(
            "Cannot serialize request body as Content-Type is not defined");
      }

      return new EqualToPattern((String) body);
    }

    switch (requestBodyContentType) {
      case MediaType.APPLICATION_JSON:
        {
          final Boolean ignoreArrayOrder = true;
          final Boolean ignoreExtraElements = true;
          final String json = toJson(body);

          return new EqualToJsonPattern(json, ignoreArrayOrder, ignoreExtraElements);
        }
      case MediaType.APPLICATION_XML:
        {
          final String xml = toXml(body);

          return new EqualToXmlPattern(xml);
        }
      default:
        throw new IllegalArgumentException(
            "Content-Type " + requestBodyContentType + " is not supported");
    }
  }

  private String getContentType(final List<String> mediaTypesOnApi, final String mediaType) {
    if (mediaType == null) {
      if (mediaTypesOnApi.isEmpty()) {
        return null;
      }

      if (mediaTypesOnApi.size() == 1) {
        return mediaTypesOnApi.get(0);
      }
    }

    if (mediaType != null) {
      for (final String candidate : mediaTypesOnApi) {
        if (candidate.equalsIgnoreCase(mediaType)) {
          return candidate;
        }
      }
    }

    if (mediaType == null) {
      throw new RuntimeException(
          "Was unable to determine media type. You need to explicitly set media type when there are several types in the API: "
              + mediaTypesOnApi.stream().collect(Collectors.joining(",")));
    } else {
      throw new RuntimeException(
          "Was unable to determine media type. Tried to mock "
              + mediaType
              + " but API has "
              + mediaTypesOnApi.stream().collect(Collectors.joining(",")));
    }
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

    final String responseBodyContentType =
        this.getContentType(
            this.handler.getResponseContentTypeList(), this.mediaTypes.getProduces());
    String body = null;
    switch (responseBodyContentType) {
      case MediaType.APPLICATION_JSON:
        {
          body = toJson(responseObject);
          break;
        }
      case MediaType.APPLICATION_XML:
        {
          body = toXml(responseObject);
          break;
        }
      default:
        throw new IllegalArgumentException(
            "Content-Type " + this.mediaTypes.getProduces() + " is not supported");
    }

    responseDefBuilder.withBody(body);

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
