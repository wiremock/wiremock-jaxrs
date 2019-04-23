package se.bjurr.wiremock.test.integration.get;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMockJaxrs.invocation;
import static com.jayway.restassured.RestAssured.given;
import static javax.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.client.ResourceInvocation;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import se.bjurr.wiremock.test.support.AcceptanceTestBase;

@RunWith(Parameterized.class)
public class WiremockJaxrsGetIntergrationTest extends AcceptanceTestBase {
  private static final Object VOID_RESPONSE = null;

  private static Logger LOG =
      Logger.getLogger(WiremockJaxrsGetIntergrationTest.class.getSimpleName());

  private final StringDTO responseObject;
  private final ResourceInvocation<TestGetResouce> invocation;
  private final List<String> expectedValidRequests;
  private final String expected;
  private final String acceptContentType;

  @Parameters(name = "{index} - {2}")
  public static Collection<Object[]> data() {
    return Arrays.asList(
        new Object[][] { //
          { //
            new StringDTO("pong"), //
            ri((r) -> r.getWithResponseObject()), //
            Arrays.asList("/getWithResponseObject"), //
            "{\n"
                + "  \"id\" : \"d68fb4e2-48ed-40d2-bc73-0a18f54f3ece\",\n"
                + "  \"request\" : {\n"
                + "    \"urlPattern\" : \".*/getWithResponseObject$\",\n"
                + "    \"method\" : \"GET\",\n"
                + "    \"headers\" : {\n"
                + "      \"Accept\" : {\n"
                + "        \"equalTo\" : \"application/json\"\n"
                + "      }\n"
                + "    }\n"
                + "  },\n"
                + "  \"response\" : {\n"
                + "    \"status\" : 202,\n"
                + "    \"body\" : \"{\\n  \\\"str\\\" : \\\"pong\\\"\\n}\",\n"
                + "    \"headers\" : {\n"
                + "      \"Content-Type\" : \"application/json\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"uuid\" : \"d68fb4e2-48ed-40d2-bc73-0a18f54f3ece\"\n"
                + "}",
            "application/json"
          }, //
          { //
            VOID_RESPONSE, //
            ri((r) -> r.getWithoutResponseObject()), //
            Arrays.asList("/getWithoutResponseObject"), //
            "{\n"
                + "  \"id\" : \"d68fb4e2-48ed-40d2-bc73-0a18f54f3ece\",\n"
                + "  \"request\" : {\n"
                + "    \"urlPattern\" : \".*/getWithoutResponseObject$\",\n"
                + "    \"method\" : \"GET\"\n"
                + "  },\n"
                + "  \"response\" : {\n"
                + "    \"status\" : 202\n"
                + "  },\n"
                + "  \"uuid\" : \"d68fb4e2-48ed-40d2-bc73-0a18f54f3ece\"\n"
                + "}",
            ""
          }, //
          { //
            new StringDTO("pong"), //
            ri((r) -> r.getWithQueryParams("value1", "value2", null)), //
            Arrays.asList(
                "/getWithQueryParams?oneparam=value1&secondparam=value2",
                "/getWithQueryParams?secondparam=value2&oneparam=value1"), //
            "{\n"
                + "  \"id\" : \"d68fb4e2-48ed-40d2-bc73-0a18f54f3ece\",\n"
                + "  \"request\" : {\n"
                + "    \"urlPattern\" : \".*/getWithQueryParams\\\\?.*\",\n"
                + "    \"method\" : \"GET\",\n"
                + "    \"headers\" : {\n"
                + "      \"Accept\" : {\n"
                + "        \"equalTo\" : \"application/json\"\n"
                + "      }\n"
                + "    },\n"
                + "    \"queryParameters\" : {\n"
                + "      \"oneparam\" : {\n"
                + "        \"equalTo\" : \"value1\"\n"
                + "      },\n"
                + "      \"secondparam\" : {\n"
                + "        \"equalTo\" : \"value2\"\n"
                + "      }\n"
                + "    }\n"
                + "  },\n"
                + "  \"response\" : {\n"
                + "    \"status\" : 202,\n"
                + "    \"body\" : \"{\\n  \\\"str\\\" : \\\"pong\\\"\\n}\",\n"
                + "    \"headers\" : {\n"
                + "      \"Content-Type\" : \"application/json\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"uuid\" : \"d68fb4e2-48ed-40d2-bc73-0a18f54f3ece\"\n"
                + "}",
            "application/json"
          },
          { //
            new StringDTO("pong"), //
            ri((r) -> r.getWithQueryParams("value1", null, "")), //
            Arrays.asList(
                "/getWithQueryParams?oneparam=value1&thirdparam=",
                "/getWithQueryParams?thirdparam=&oneparam=value1"), //
            "{\n"
                + "  \"id\" : \"d68fb4e2-48ed-40d2-bc73-0a18f54f3ece\",\n"
                + "  \"request\" : {\n"
                + "    \"urlPattern\" : \".*/getWithQueryParams\\\\?.*\",\n"
                + "    \"method\" : \"GET\",\n"
                + "    \"headers\" : {\n"
                + "      \"Accept\" : {\n"
                + "        \"equalTo\" : \"application/json\"\n"
                + "      }\n"
                + "    },\n"
                + "    \"queryParameters\" : {\n"
                + "      \"oneparam\" : {\n"
                + "        \"equalTo\" : \"value1\"\n"
                + "      },\n"
                + "      \"thirdparam\" : {\n"
                + "        \"equalTo\" : \"\"\n"
                + "      }\n"
                + "    }\n"
                + "  },\n"
                + "  \"response\" : {\n"
                + "    \"status\" : 202,\n"
                + "    \"body\" : \"{\\n  \\\"str\\\" : \\\"pong\\\"\\n}\",\n"
                + "    \"headers\" : {\n"
                + "      \"Content-Type\" : \"application/json\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"uuid\" : \"d68fb4e2-48ed-40d2-bc73-0a18f54f3ece\"\n"
                + "}",
            "application/json"
          },
          { //
            new StringDTO("pong"), //
            ri((r) -> r.getWithPathParams("value1", "value2", "value3")), //
            Arrays.asList("/getWithPathParams/value1/value2/value3"), //
            "{\n"
                + "  \"id\" : \"d68fb4e2-48ed-40d2-bc73-0a18f54f3ece\",\n"
                + "  \"request\" : {\n"
                + "    \"urlPattern\" : \".*/getWithPathParams/value1/value2/value3$\",\n"
                + "    \"method\" : \"GET\",\n"
                + "    \"headers\" : {\n"
                + "      \"Accept\" : {\n"
                + "        \"equalTo\" : \"application/json\"\n"
                + "      }\n"
                + "    }\n"
                + "  },\n"
                + "  \"response\" : {\n"
                + "    \"status\" : 202,\n"
                + "    \"body\" : \"{\\n  \\\"str\\\" : \\\"pong\\\"\\n}\",\n"
                + "    \"headers\" : {\n"
                + "      \"Content-Type\" : \"application/json\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"uuid\" : \"d68fb4e2-48ed-40d2-bc73-0a18f54f3ece\"\n"
                + "}",
            "application/json"
          }
        } //
        );
  }

  private static ResourceInvocation<TestGetResouce> ri(final ResourceInvocation<TestGetResouce> r) {
    return r;
  }

  public WiremockJaxrsGetIntergrationTest(
      final StringDTO responseObject,
      final ResourceInvocation<TestGetResouce> invocation,
      final List<String> expectedValidRequests,
      final String expected,
      final String acceptContentType) {
    this.responseObject = responseObject;
    this.invocation = invocation;
    this.expectedValidRequests = expectedValidRequests;
    this.expected = expected;
    this.acceptContentType = acceptContentType;
  }

  @Test
  public void assertResponse() {
    StubMapping sm;
    if (responseObject == null) {
      sm =
          stubFor( //
              invocation(TestGetResouce.class, invocation) //
                  .willReturn(aResponse().withStatus(SC_ACCEPTED)));
    } else {
      sm =
          stubFor( //
              invocation(TestGetResouce.class, invocation) //
                  .willReturn(aResponse().withStatus(SC_ACCEPTED), responseObject));
    }

    final String actual = StubMapping.buildJsonStringFor(clean(sm));
    LOG.info("\n" + actual);
    assertThat(actual) //
        .isEqualTo(expected);

    for (final String path : expectedValidRequests) {
      given() //
          .accept(acceptContentType) //
          .get(path) //
          .then()
          .assertThat() //
          .statusCode(SC_ACCEPTED) //
          .and() //
          .contentType(acceptContentType);
    }
  }

  private StubMapping clean(final StubMapping sm) {
    sm.setUuid(UUID.fromString("d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"));
    sm.setId(UUID.fromString("d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"));
    return sm;
  }
}
