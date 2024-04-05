package se.bjurr.wiremock.test.testcases;

import static io.restassured.RestAssured.given;
import static jakarta.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static java.util.Arrays.asList;
import static se.bjurr.wiremock.test.testutils.InvocationAssertion.assertThatApi;

import org.junit.Test;
import se.bjurr.wiremock.test.example_apis.ListResouce;
import se.bjurr.wiremock.test.example_apis.model.ItemDTO;
import se.bjurr.wiremock.test.testutils.AcceptanceTestBase;

public class ListTest extends AcceptanceTestBase {
  @Test
  public void getItems() {
    assertThatApi(ListResouce.class)
        .andWillReturn(asList(new ItemDTO("pong")))
        .isInvokedLike((r) -> r.getItems())
        .shouldTranslateToMapping(
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
  public void getWithoutResponseObject() {
    assertThatApi(ListResouce.class)
        .isInvokedLike((r) -> r.create(asList(new ItemDTO("pong"))))
        .shouldTranslateToMapping(
            """
{
  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
  "request" : {
    "urlPattern" : ".*/create$",
    "method" : "POST",
    "headers" : {
      "Content-Type" : {
        "equalTo" : "application/json"
      }
    },
    "bodyPatterns" : [ {
      "equalToJson" : "[ {\\n  \\"str\\" : \\"pong\\",\\n  \\"id\\" : 0\\n} ]",
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
  }
}
