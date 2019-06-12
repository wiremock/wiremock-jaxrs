package se.bjurr.wiremock.test.integration.post;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMockJaxrs.invocation;
import static io.restassured.RestAssured.given;
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
public class WiremockJaxrsPostIntergrationTest extends AcceptanceTestBase {
  private static final Object VOID_RESPONSE = null;

  private static Logger LOG =
      Logger.getLogger(WiremockJaxrsPostIntergrationTest.class.getSimpleName());

  private final StringDTO responseObject;
  private final ResourceInvocation<TestPostResouce> invocation;
  private final List<String> expectedValidRequests;
  private final String expected;

  private final String acceptContentType;

  private final String contentType;

  private final Object postContent;

  @Parameters(name = "{index} - {2}")
  public static Collection<Object[]> data() {
    final StringDTO stringDtoCreate = new StringDTO("create");
    final StringDTO stringDtoCreated = new StringDTO("create");
    stringDtoCreated.setId(123);

    return Arrays.asList(
        new Object[][] { //
          { //
            new StringDTO("pong"), //
            stringDtoCreate,
            ri((r) -> r.createStringWithResponse(stringDtoCreate)), //
            Arrays.asList("/createStringWithResponse"), //
            "{\n"
                + "  \"id\" : \"d68fb4e2-48ed-40d2-bc73-0a18f54f3ece\",\n"
                + "  \"request\" : {\n"
                + "    \"urlPattern\" : \".*/createStringWithResponse$\",\n"
                + "    \"method\" : \"POST\",\n"
                + "    \"headers\" : {\n"
                + "      \"Content-Type\" : {\n"
                + "        \"equalTo\" : \"application/json\"\n"
                + "      },\n"
                + "      \"Accept\" : {\n"
                + "        \"equalTo\" : \"application/json\"\n"
                + "      }\n"
                + "    },\n"
                + "    \"bodyPatterns\" : [ {\n"
                + "      \"equalToJson\" : \"{\\n  \\\"str\\\" : \\\"create\\\"\\n}\",\n"
                + "      \"ignoreArrayOrder\" : true,\n"
                + "      \"ignoreExtraElements\" : true\n"
                + "    } ]\n"
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
            "application/json", // Resp type
            "application/json" // Req type
          }, //
          { //
            VOID_RESPONSE, //
            stringDtoCreate,
            ri((r) -> r.createStringWithoutResponse(stringDtoCreate)), //
            Arrays.asList("/createStringWithoutResponse"), //
            "{\n"
                + "  \"id\" : \"d68fb4e2-48ed-40d2-bc73-0a18f54f3ece\",\n"
                + "  \"request\" : {\n"
                + "    \"urlPattern\" : \".*/createStringWithoutResponse$\",\n"
                + "    \"method\" : \"POST\",\n"
                + "    \"headers\" : {\n"
                + "      \"Content-Type\" : {\n"
                + "        \"equalTo\" : \"application/json\"\n"
                + "      }\n"
                + "    },\n"
                + "    \"bodyPatterns\" : [ {\n"
                + "      \"equalToJson\" : \"{\\n  \\\"str\\\" : \\\"create\\\"\\n}\",\n"
                + "      \"ignoreArrayOrder\" : true,\n"
                + "      \"ignoreExtraElements\" : true\n"
                + "    } ]\n"
                + "  },\n"
                + "  \"response\" : {\n"
                + "    \"status\" : 202\n"
                + "  },\n"
                + "  \"uuid\" : \"d68fb4e2-48ed-40d2-bc73-0a18f54f3ece\"\n"
                + "}",
            "", // Resp type
            "application/json" // Req type
          }, //
        } //
        );
  }

  private static ResourceInvocation<TestPostResouce> ri(
      final ResourceInvocation<TestPostResouce> r) {
    return r;
  }

  public WiremockJaxrsPostIntergrationTest(
      final StringDTO responseObject,
      final Object postContent,
      final ResourceInvocation<TestPostResouce> invocation,
      final List<String> expectedValidRequests,
      final String expected,
      final String acceptContentType,
      final String contentType) {
    this.responseObject = responseObject;
    this.invocation = invocation;
    this.expectedValidRequests = expectedValidRequests;
    this.expected = expected;
    this.acceptContentType = acceptContentType;
    this.contentType = contentType;
    this.postContent = postContent;
  }

  @Test
  public void assertResponse() {
    StubMapping sm;
    if (responseObject == null) {
      sm =
          stubFor( //
              invocation(TestPostResouce.class, invocation) //
                  .willReturn(aResponse().withStatus(SC_ACCEPTED)));
    } else {
      sm =
          stubFor( //
              invocation(TestPostResouce.class, invocation) //
                  .willReturn(aResponse().withStatus(SC_ACCEPTED), responseObject));
    }

    final String actual = StubMapping.buildJsonStringFor(clean(sm));
    LOG.info("\n" + actual);
    assertThat(actual) //
        .isEqualTo(expected);

    for (final String path : expectedValidRequests) {
      given() //
          .accept(acceptContentType) //
          .contentType(contentType) //
          .request() //
          .body(postContent) //
          .post(path) //
          .then()
          .assertThat() //
          .contentType(acceptContentType) //
          .statusCode(SC_ACCEPTED);
    }
  }

  private StubMapping clean(final StubMapping sm) {
    sm.setUuid(UUID.fromString("d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"));
    sm.setId(UUID.fromString("d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"));
    return sm;
  }
}
