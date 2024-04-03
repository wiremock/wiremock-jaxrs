package se.bjurr.wiremock.test.testcases;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.jaxrs.api.WireMockJaxrs.invocation;
import static io.restassured.RestAssured.given;
import static jakarta.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.junit.Test;
import se.bjurr.wiremock.test.example_apis.resource_with_list.ItemDTO;
import se.bjurr.wiremock.test.example_apis.resource_with_list.ListResouce;
import se.bjurr.wiremock.test.testutils.AcceptanceTestBase;

public class UsageExamplesTest extends AcceptanceTestBase {

  @Test
  public void getItems() {
    final StubMapping sm =
        stubFor( //
            invocation(ListResouce.class, (r) -> r.getItems()) //
                .willReturn(aResponse().withStatus(SC_ACCEPTED), asList(new ItemDTO("pong"))));

    final String actual = StubMapping.buildJsonStringFor(this.setStaticUUIDs(sm));

    assertThat(actual) //
        .isEqualToIgnoringWhitespace(
            """
        		{
      		  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
      		  "request" : {
      		    "urlPattern" : ".*/list$",
      		    "method" : "GET",
      		    "headers" : {
      		      "Accept" : {
      		        "equalTo" : "application/json"
      		      }
      		    }
      		  },
      		  "response" : {
      		    "status" : 202,
      		    "body" : "[ {\\n  \\"str\\" : \\"pong\\",\\n  \\"id\\" : 0\\n} ]",
      		    "headers" : {
      		      "Content-Type" : "application/json"
      		    }
      		  },
      		  "uuid" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"
      		}
      		""");

    given() //
        .accept("application/json") //
        .get("/list") //
        .then()
        .assertThat() //
        .statusCode(SC_ACCEPTED) //
        .and() //
        .contentType("application/json");
  }

  @Test
  public void createItem() {
    final StubMapping sm =
        stubFor( //
            invocation(ListResouce.class, (r) -> r.post(new ItemDTO("the item"))) //
                .willReturn(
                    aResponse().withStatus(SC_ACCEPTED), new ItemDTO("the item").setId(123)));

    final String actual = StubMapping.buildJsonStringFor(this.setStaticUUIDs(sm));

    assertThat(actual) //
        .isEqualToIgnoringWhitespace(
            """
    		{
      		  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
      		  "request" : {
      		    "urlPattern" : ".*/create$",
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
      		      "equalToJson" : "{\\n  \\"str\\" : \\"the item\\",\\n  \\"id\\" : 0\\n}",
      		      "ignoreArrayOrder" : true,
      		      "ignoreExtraElements" : true
      		    } ]
      		  },
      		  "response" : {
      		    "status" : 202,
      		    "body" : "{\\n  \\"str\\" : \\"the item\\",\\n  \\"id\\" : 123\\n}",
      		    "headers" : {
      		      "Content-Type" : "application/json"
      		    }
      		  },
      		  "uuid" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"
      		}
      		""");

    given() //
        .accept("application/json") //
        .contentType("application/json") //
        .request() //
        .body("""
{
  "str" : "the item",
  "id" : 0
}
""") //
        .post("/create") //
        .then()
        .assertThat() //
        .contentType("application/json") //
        .statusCode(SC_ACCEPTED);
  }
}
