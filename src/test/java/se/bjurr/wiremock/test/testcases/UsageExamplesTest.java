package se.bjurr.wiremock.test.testcases;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.jaxrs.api.MediaTypes.withMediaTypes;
import static com.github.tomakehurst.wiremock.jaxrs.api.WireMockJaxrs.invocation;
import static io.restassured.RestAssured.given;
import static jakarta.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import jakarta.ws.rs.core.MediaType;
import org.junit.Test;
import se.bjurr.wiremock.test.example_apis.ExampleApi;
import se.bjurr.wiremock.test.example_apis.model.StringDTO;
import se.bjurr.wiremock.test.testutils.AcceptanceTestBase;

public class UsageExamplesTest extends AcceptanceTestBase {

  @Test
  public void getWithSingleProduces() {
    final StubMapping sm =
        stubFor( //
            invocation(ExampleApi.class, (r) -> r.getWithSingleProduces()) //
                .willReturn(aResponse().withStatus(SC_ACCEPTED), new StringDTO("whatever")));

    final String actual = StubMapping.buildJsonStringFor(this.setStaticUUIDs(sm));

    assertThat(actual) //
        .isEqualToIgnoringWhitespace(
            """
{
  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
  "request" : {
    "urlPattern" : ".*/getWithSingleProduces$",
    "method" : "GET",
    "headers" : {
      "Accept" : {
        "equalTo" : "application/json"
      }
    }
  },
  "response" : {
    "status" : 202,
    "body" : "{\\n  \\"str\\" : \\"whatever\\"\\n}",
    "headers" : {
      "Content-Type" : "application/json"
    }
  },
  "uuid" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"
}
""");

    given() //
        .accept("application/json") //
        .get("/getWithSingleProduces") //
        .then()
        .assertThat() //
        .statusCode(SC_ACCEPTED) //
        .and() //
        .contentType("application/json");
  }

  @Test
  public void getWithMultipleProduces() {
    final StubMapping sm =
        stubFor( //
            invocation(
                    ExampleApi.class,
                    (r) -> r.getWithMultipleProduces(),
                    withMediaTypes().producing(MediaType.APPLICATION_JSON)) //
                .willReturn(aResponse().withStatus(SC_ACCEPTED), new StringDTO("whatever")));

    final String actual = StubMapping.buildJsonStringFor(this.setStaticUUIDs(sm));

    assertThat(actual) //
        .isEqualToIgnoringWhitespace(
            """
{
  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
  "request" : {
    "urlPattern" : ".*/getWithMultipleProduces$",
    "method" : "GET",
    "headers" : {
      "Accept" : {
        "equalTo" : "application/json"
      }
    }
  },
  "response" : {
    "status" : 202,
    "body" : "{\\n  \\"str\\" : \\"whatever\\"\\n}",
    "headers" : {
      "Content-Type" : "application/json"
    }
  },
  "uuid" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"
}
""");

    given() //
        .accept("application/json") //
        .get("/getWithMultipleProduces") //
        .then()
        .assertThat() //
        .statusCode(SC_ACCEPTED) //
        .and() //
        .contentType("application/json");
  }
}
