package se.bjurr.wiremock.test.testcases;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.jaxrs.api.WireMockJaxrs.invocation;
import static io.restassured.RestAssured.given;
import static jakarta.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.client.ResourceInvocation;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import org.junit.Test;
import se.bjurr.wiremock.test.example_apis.resource_with_get_and_params.StringDTO;
import se.bjurr.wiremock.test.example_apis.resource_with_get_and_params.TestGetResouce;
import se.bjurr.wiremock.test.testutils.AcceptanceTestBase;

public class WiremockJaxrsGetIntergrationTest extends AcceptanceTestBase {
  private static final Object VOID_RESPONSE = null;

  private static Logger LOG =
      Logger.getLogger(WiremockJaxrsGetIntergrationTest.class.getSimpleName());

  @Test
  public void getWithResponseObject() {
    this.test(
        new StringDTO("pong"), //
        (r) -> r.getWithResponseObject(), //
        Arrays.asList("/getWithResponseObject"), //
        """
					{
					  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
					  "request" : {
					    "urlPattern" : ".*/getWithResponseObject$",
					    "method" : "GET",
					    "headers" : {
					      "Accept" : {
					        "equalTo" : "application/json"
					      }
					    }
					  },
					  "response" : {
					    "status" : 202,
					    "body" : "{\\n  \\"str\\" : \\"pong\\"\\n}",
					    "headers" : {
					      "Content-Type" : "application/json"
					    }
					  },
					  "uuid" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"
					}""",
        "application/json");
  }

  @Test
  public void getWithoutResponseObject() {
    this.test(
        VOID_RESPONSE, //
        (r) -> r.getWithoutResponseObject(), //
        Arrays.asList("/getWithoutResponseObject"), //
        """
					{
					  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
					  "request" : {
					    "urlPattern" : ".*/getWithoutResponseObject$",
					    "method" : "GET"
					  },
					  "response" : {
					    "status" : 202
					  },
					  "uuid" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"
					}""",
        "");
  }

  @Test
  public void getWithQueryParams_value1_value2_null() {
    this.test(
        new StringDTO("pong"), //
        (r) -> r.getWithQueryParams("value1", "value2", null), //
        Arrays.asList(
            "/getWithQueryParams?oneparam=value1&secondparam=value2",
            "/getWithQueryParams?secondparam=value2&oneparam=value1"), //
        """
					{
					  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
					  "request" : {
					    "urlPattern" : ".*/getWithQueryParams\\\\?.*",
					    "method" : "GET",
					    "headers" : {
					      "Accept" : {
					        "equalTo" : "application/json"
					      }
					    },
					    "queryParameters" : {
					      "oneparam" : {
					        "equalTo" : "value1"
					      },
					      "secondparam" : {
					        "equalTo" : "value2"
					      }
					    }
					  },
					  "response" : {
					    "status" : 202,
					    "body" : "{\\n  \\"str\\" : \\"pong\\"\\n}",
					    "headers" : {
					      "Content-Type" : "application/json"
					    }
					  },
					  "uuid" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"
					}""",
        "application/json");
  }

  @Test
  public void getWithQueryParams_value1_null_empty() {
    this.test(
        new StringDTO("pong"), //
        (r) -> r.getWithQueryParams("value1", null, ""), //
        Arrays.asList(
            "/getWithQueryParams?oneparam=value1&thirdparam=",
            "/getWithQueryParams?thirdparam=&oneparam=value1"), //
        """
					{
					  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
					  "request" : {
					    "urlPattern" : ".*/getWithQueryParams\\\\?.*",
					    "method" : "GET",
					    "headers" : {
					      "Accept" : {
					        "equalTo" : "application/json"
					      }
					    },
					    "queryParameters" : {
					      "oneparam" : {
					        "equalTo" : "value1"
					      },
					      "thirdparam" : {
					        "equalTo" : ""
					      }
					    }
					  },
					  "response" : {
					    "status" : 202,
					    "body" : "{\\n  \\"str\\" : \\"pong\\"\\n}",
					    "headers" : {
					      "Content-Type" : "application/json"
					    }
					  },
					  "uuid" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"
					}""",
        "application/json");
  }

  @Test
  public void getWithPathParams_value1_value2_value3() {
    this.test(
        new StringDTO("pong"), //
        (r) -> r.getWithPathParams("value1", "value2", "value3"), //
        Arrays.asList("/getWithPathParams/value1/value2/value3"), //
        """
					{
					  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
					  "request" : {
					    "urlPattern" : ".*/getWithPathParams/value1/value2/value3$",
					    "method" : "GET",
					    "headers" : {
					      "Accept" : {
					        "equalTo" : "application/json"
					      }
					    }
					  },
					  "response" : {
					    "status" : 202,
					    "body" : "{\\n  \\"str\\" : \\"pong\\"\\n}",
					    "headers" : {
					      "Content-Type" : "application/json"
					    }
					  },
					  "uuid" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"
					}""",
        "application/json");
  }

  public void test(
      final Object responseObject,
      final ResourceInvocation<TestGetResouce> invocation,
      final List<String> expectedValidRequests,
      final String expected,
      final String acceptContentType) {
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

    final String actual = StubMapping.buildJsonStringFor(this.setStaticUUIDs(sm));
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
}
