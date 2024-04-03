package se.bjurr.wiremock.test.testcases;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.jaxrs.api.WireMockJaxrs.invocation;
import static io.restassured.RestAssured.given;
import static jakarta.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.client.ResourceInvocation;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import java.util.logging.Logger;
import org.junit.Test;
import se.bjurr.wiremock.test.example_apis.resource_with_post_and_params.StringDTO;
import se.bjurr.wiremock.test.example_apis.resource_with_post_and_params.TestPostResouce;
import se.bjurr.wiremock.test.testutils.AcceptanceTestBase;

public class WiremockJaxrsPostIntergrationTest extends AcceptanceTestBase {
  private static final Object VOID_RESPONSE = null;

  private static Logger LOG =
      Logger.getLogger(WiremockJaxrsPostIntergrationTest.class.getSimpleName());

  @Test
  public void createStringWithResponse() {
    final StringDTO stringDtoCreate = new StringDTO("create");
    final StringDTO stringDtoCreated = new StringDTO("create");
    stringDtoCreated.setId(123);
    this.newWiremockJaxrsPostIntergrationTest(
        new StringDTO("pong"), //
        stringDtoCreate,
        (r) -> r.createStringWithResponse(stringDtoCreate), //
        "/createStringWithResponse", //
        """
					{
					  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
					  "request" : {
					    "urlPattern" : ".*/createStringWithResponse$",
					    "method" : "POST",
					    "headers" : {
					      "Content-Type" : {
					        "equalTo" : "application/json"
					      },
					      "Accept" : {
					        "equalTo" : "application/json"
					      }
					    },
					    "bodyPatterns" : [ {
					      "equalToJson" : "{\\n  \\"str\\" : \\"create\\"\\n}",
					      "ignoreArrayOrder" : true,
					      "ignoreExtraElements" : true
					    } ]
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
        "application/json",
        "application/json");
  }

  @Test
  public void createStringWithoutResponse() {
    final StringDTO stringDtoCreate = new StringDTO("create");
    final StringDTO stringDtoCreated = new StringDTO("create");
    stringDtoCreated.setId(123);
    this.newWiremockJaxrsPostIntergrationTest(
        VOID_RESPONSE, //
        stringDtoCreate,
        (r) -> r.createStringWithoutResponse(stringDtoCreate), //
        "/createStringWithoutResponse", //
        """
					{
					  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
					  "request" : {
					    "urlPattern" : ".*/createStringWithoutResponse$",
					    "method" : "POST",
					    "headers" : {
					      "Content-Type" : {
					        "equalTo" : "application/json"
					      }
					    },
					    "bodyPatterns" : [ {
					      "equalToJson" : "{\\n  \\"str\\" : \\"create\\"\\n}",
					      "ignoreArrayOrder" : true,
					      "ignoreExtraElements" : true
					    } ]
					  },
					  "response" : {
					    "status" : 202
					  },
					  "uuid" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"
					}""",
        "",
        "application/json");
  }

  @Test
  public void createXmlStringWithoutResponse() {
    final StringDTO stringDtoCreate = new StringDTO("create");
    final StringDTO stringDtoCreated = new StringDTO("create");
    stringDtoCreated.setId(123);
    this.newWiremockJaxrsPostIntergrationTest(
        VOID_RESPONSE, //
        stringDtoCreate,
        (r) -> r.createXmlStringWithoutResponse(stringDtoCreate), //
        "/createXmlStringWithoutResponse", //
        """
					{
					  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
					  "request" : {
					    "urlPattern" : ".*/createXmlStringWithoutResponse$",
					    "method" : "POST",
					    "headers" : {
					      "Content-Type" : {
					        "equalTo" : "application/xml"
					      }
					    },
					    "bodyPatterns" : [ {
					      "equalToXml" : "<StringDTO>\\n  <str>create</str>\\n</StringDTO>\\n"
					    } ]
					  },
					  "response" : {
					    "status" : 202
					  },
					  "uuid" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"
					}""",
        "",
        "application/xml");
  }

  public void newWiremockJaxrsPostIntergrationTest(
      final Object responseObject,
      final Object postContent,
      final ResourceInvocation<TestPostResouce> invocation,
      final String expectedValidRequest,
      final String expected,
      final String acceptContentType,
      final String contentType) {
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

    final String actual = StubMapping.buildJsonStringFor(this.setStaticUUIDs(sm));
    LOG.info("\n" + actual);
    assertThat(actual) //
        .isEqualTo(expected);

    given() //
        .accept(acceptContentType) //
        .contentType(contentType) //
        .request() //
        .body(postContent) //
        .post(expectedValidRequest) //
        .then()
        .assertThat() //
        .contentType(acceptContentType) //
        .statusCode(SC_ACCEPTED);
  }
}
