package se.bjurr.wiremock.test.testutils;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.jaxrs.api.WireMockJaxrs.invocation;
import static jakarta.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import com.github.tomakehurst.wiremock.client.ResourceInvocation;
import com.github.tomakehurst.wiremock.jaxrs.api.MediaTypes;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import java.util.UUID;

public class InvocationAssertion<T> {
  private Class<T> clazz;
  private ResourceInvocation<T> invocation;
  private final MediaTypes mediaTypes;
  private Object responseObject;

  private InvocationAssertion() {
    this.mediaTypes = MediaTypes.withMediaTypes();
  }

  public static <T> InvocationAssertion<T> assertThatApi(final Class<T> clazz) {
    return new InvocationAssertion<T>().whenApi(clazz);
  }

  public InvocationAssertion<T> whenApi(final Class<T> clazz) {
    this.clazz = clazz;
    return this;
  }

  public InvocationAssertion<T> isInvokedLike(final ResourceInvocation<T> invocation) {
    this.invocation = invocation;
    return this;
  }

  public InvocationAssertion<T> withConsumingMediaType(final String mediaType) {
    this.mediaTypes.consuming(mediaType);
    return this;
  }

  public InvocationAssertion<T> withProducingMediaType(final String mediaType) {
    this.mediaTypes.producing(mediaType);
    return this;
  }

  public InvocationAssertion<T> andWillReturn(final Object responseObject) {
    this.responseObject = responseObject;
    return this;
  }

  public InvocationAssertion<T> shouldTranslateToMapping(final String expectedJson) {
    if (this.responseObject == null) {
      this.assertInvocationTranslatesToMappingJson(this.clazz, this.invocation, expectedJson);
    } else {
      this.assertInvocationTranslatesToMappingJson(
          this.clazz, this.invocation, this.responseObject, expectedJson);
    }

    return this;
  }

  public void shouldThrow(final String exceptionMessage) {
    final Exception thrown =
        assertThrows(
            Exception.class,
            () -> this.shouldTranslateToMapping("""
		Should have thrown exception!
		"""));
    assertThat(thrown.getMessage()).isEqualToIgnoringWhitespace(exceptionMessage);
  }

  private <T> void assertInvocationTranslatesToMappingJson(
      final Class<T> clazz,
      final ResourceInvocation<T> invocation,
      final Object responseObject,
      final String expectedJson) {
    StubMapping sm = null;
    if (this.mediaTypes == null) {
      sm =
          stubFor( //
              invocation(clazz, invocation) //
                  .willReturn(aResponse().withStatus(SC_ACCEPTED), responseObject));
    } else {
      sm =
          stubFor( //
              invocation(clazz, invocation, this.mediaTypes) //
                  .willReturn(aResponse().withStatus(SC_ACCEPTED), responseObject));
    }

    final String actual = StubMapping.buildJsonStringFor(this.setStaticUUIDs(sm));

    assertThat(actual) //
        .isEqualToIgnoringWhitespace(expectedJson);
  }

  private <T> void assertInvocationTranslatesToMappingJson(
      final Class<T> clazz, final ResourceInvocation<T> invocation, final String expected) {
    StubMapping sm = null;
    if (this.mediaTypes == null) {
      sm =
          stubFor( //
              invocation(clazz, invocation) //
                  .willReturn(aResponse().withStatus(SC_ACCEPTED)));
    } else {
      sm =
          stubFor( //
              invocation(clazz, invocation, this.mediaTypes) //
                  .willReturn(aResponse().withStatus(SC_ACCEPTED)));
    }

    final String actual = StubMapping.buildJsonStringFor(this.setStaticUUIDs(sm));

    assertThat(actual) //
        .isEqualToIgnoringWhitespace(expected);
  }

  private StubMapping setStaticUUIDs(final StubMapping sm) {
    sm.setUuid(UUID.fromString("d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"));
    sm.setId(UUID.fromString("d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"));
    return sm;
  }
}
