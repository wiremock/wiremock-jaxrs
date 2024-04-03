package se.bjurr.wiremock.test.testcases;

import static io.restassured.RestAssured.given;
import static jakarta.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static se.bjurr.wiremock.test.testutils.InvocationAssertion.assertThatApi;

import org.junit.Test;
import se.bjurr.wiremock.test.example_apis.resource_with_get_and_params.GetResouce;
import se.bjurr.wiremock.test.example_apis.resource_with_get_and_params.StringDTO;
import se.bjurr.wiremock.test.testutils.AcceptanceTestBase;

public class GetIntergrationTest extends AcceptanceTestBase {
  @Test
  public void getWithResponseObject() {
    assertThatApi(GetResouce.class)
        .andRespondingWith(new StringDTO("pong"))
        .isInvokedLike((r) -> r.getWithResponseObject())
        .shouldTranslateToMapping(
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
			}
			""");

    given() //
        .accept("application/json") //
        .get("/getWithResponseObject") //
        .then()
        .assertThat() //
        .statusCode(SC_ACCEPTED) //
        .and() //
        .contentType("application/json");
  }

  @Test
  public void getWithoutResponseObject() {
    assertThatApi(GetResouce.class)
        .isInvokedLike((r) -> r.getWithoutResponseObject())
        .shouldTranslateToMapping(
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
}
""");

    given() //
        .accept("application/json") //
        .get("/getWithoutResponseObject") //
        .then()
        .assertThat() //
        .statusCode(SC_ACCEPTED);
  }

  @Test
  public void getWithQueryParams_value1_value2_null() {
    assertThatApi(GetResouce.class)
        .andRespondingWith(new StringDTO("pong"))
        .isInvokedLike((r) -> r.getWithQueryParams("value1", "value2", null))
        .shouldTranslateToMapping(
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
}
""");

    given() //
        .accept("application/json") //
        .get("/getWithQueryParams?oneparam=value1&secondparam=value2") //
        .then()
        .assertThat() //
        .statusCode(SC_ACCEPTED) //
        .and() //
        .contentType("application/json");

    given() //
        .accept("application/json") //
        .get("/getWithQueryParams?secondparam=value2&oneparam=value1") //
        .then()
        .assertThat() //
        .statusCode(SC_ACCEPTED) //
        .and() //
        .contentType("application/json");
  }

  @Test
  public void getWithQueryParams_value1_null_empty() {

    assertThatApi(GetResouce.class)
        .andRespondingWith(new StringDTO("pong"))
        .isInvokedLike((r) -> r.getWithQueryParams("value1", null, ""))
        .shouldTranslateToMapping(
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
}
""");

    given() //
        .accept("application/json") //
        .get("/getWithQueryParams?thirdparam=&oneparam=value1") //
        .then()
        .assertThat() //
        .statusCode(SC_ACCEPTED) //
        .and() //
        .contentType("application/json");
    given() //
        .accept("application/json") //
        .get("/getWithQueryParams?oneparam=value1&thirdparam=") //
        .then()
        .assertThat() //
        .statusCode(SC_ACCEPTED) //
        .and() //
        .contentType("application/json");
  }

  @Test
  public void getWithPathParams_value1_value2_value3() {
    assertThatApi(GetResouce.class)
        .andRespondingWith(new StringDTO("pong"))
        .isInvokedLike((r) -> r.getWithPathParams("value1", "value2", "value3"))
        .shouldTranslateToMapping(
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
}
""");

    given() //
        .accept("application/json") //
        .get("/getWithPathParams/value1/value2/value3") //
        .then()
        .assertThat() //
        .statusCode(SC_ACCEPTED) //
        .and() //
        .contentType("application/json");
  }
}
