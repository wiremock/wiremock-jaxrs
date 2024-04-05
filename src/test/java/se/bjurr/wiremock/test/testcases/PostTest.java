package se.bjurr.wiremock.test.testcases;

import static io.restassured.RestAssured.given;
import static jakarta.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static se.bjurr.wiremock.test.testutils.InvocationAssertion.assertThatApi;

import org.junit.Test;
import se.bjurr.wiremock.test.example_apis.PostResouce;
import se.bjurr.wiremock.test.example_apis.model.StringDTO;
import se.bjurr.wiremock.test.testutils.AcceptanceTestBase;

public class PostTest extends AcceptanceTestBase {
  @Test
  public void createStringWithResponse() {
    final StringDTO stringDtoCreate = new StringDTO("create");

    assertThatApi(PostResouce.class)
        .isInvokedLike((r) -> r.createStringWithResponse(stringDtoCreate))
        .andWillReturn(new StringDTO("pong"))
        .shouldTranslateToMapping(
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
			}
			""");

    given() //
        .accept("application/json") //
        .contentType("application/json") //
        .request() //
        .body(stringDtoCreate) //
        .post("/createStringWithResponse") //
        .then()
        .assertThat() //
        .contentType("application/json") //
        .statusCode(SC_ACCEPTED);
  }

  @Test
  public void createStringWithoutResponse() {
    final StringDTO stringDtoCreate = new StringDTO("create");

    assertThatApi(PostResouce.class)
        .isInvokedLike((r) -> r.createStringWithoutResponse(stringDtoCreate))
        .shouldTranslateToMapping(
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
	}
	""");

    given() //
        .accept("") //
        .contentType("application/json") //
        .request() //
        .body(stringDtoCreate) //
        .post("/createStringWithoutResponse") //
        .then()
        .assertThat() //
        .contentType("") //
        .statusCode(SC_ACCEPTED);
  }

  @Test
  public void createXmlStringWithoutResponse() {
    final StringDTO stringDtoCreate = new StringDTO("create");

    assertThatApi(PostResouce.class)
        .isInvokedLike((r) -> r.createXmlStringWithoutResponse(stringDtoCreate))
        .shouldTranslateToMapping(
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
			}
			""");

    given() //
        .accept("") //
        .contentType("application/xml") //
        .request() //
        .body(stringDtoCreate) //
        .post("/createXmlStringWithoutResponse") //
        .then()
        .assertThat() //
        .contentType("") //
        .statusCode(SC_ACCEPTED);
  }
}
