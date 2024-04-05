package se.bjurr.wiremock.test.testcases;

import static se.bjurr.wiremock.test.testutils.InvocationAssertion.assertThatApi;

import jakarta.ws.rs.core.MediaType;
import org.junit.Test;
import se.bjurr.wiremock.test.example_apis.MultipleConsumes;
import se.bjurr.wiremock.test.example_apis.model.StringDTO;
import se.bjurr.wiremock.test.testutils.AcceptanceTestBase;

public class MultipleConsumesTest extends AcceptanceTestBase {
  @Test
  public void json() {
    assertThatApi(MultipleConsumes.class)
        .isInvokedLike((r) -> r.consumesXmlAndJson(new StringDTO("whatever")))
        .withConsumingMediaType(MediaType.APPLICATION_JSON)
        .shouldTranslateToMapping(
            """
{
  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
  "request" : {
    "urlPattern" : ".*/consumesXmlOrJson$",
    "method" : "POST",
    "headers" : {
      "Content-Type" : {
        "equalTo" : "application/json"
      }
    },
    "bodyPatterns" : [ {
      "equalToJson" : "{\\n  \\"str\\" : \\"whatever\\"\\n}",
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

  @Test
  public void xml() {
    assertThatApi(MultipleConsumes.class)
        .isInvokedLike((r) -> r.consumesXmlAndJson(new StringDTO("whatever")))
        .withConsumingMediaType(MediaType.APPLICATION_XML)
        .shouldTranslateToMapping(
            """
{
  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
  "request" : {
    "urlPattern" : ".*/consumesXmlOrJson$",
    "method" : "POST",
    "headers" : {
      "Content-Type" : {
        "equalTo" : "application/xml"
      }
    },
    "bodyPatterns" : [ {
      "equalToXml" : "<StringDTO>\\n  <str>whatever</str>\\n</StringDTO>\\n"
    } ]
  },
  "response" : {
    "status" : 202
  },
  "uuid" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"
}
""");
  }

  @Test
  public void octet() {
    assertThatApi(MultipleConsumes.class)
        .isInvokedLike((r) -> r.consumesXmlAndJson(new StringDTO("whatever")))
        .withConsumingMediaType(MediaType.APPLICATION_OCTET_STREAM)
        .shouldThrow(
            """
		    			Was unable to determine media type. Tried to mock application/octet-stream but API has application/xml,application/json
		    			""");
  }

  @Test
  public void no_explicit_type_given() {
    assertThatApi(MultipleConsumes.class)
        .isInvokedLike((r) -> r.consumesXmlAndJson(new StringDTO("whatever")))
        .shouldThrow(
            """
		    			Was unable to determine media type. You need to explicitly set media type when there are several types in the API: application/xml,application/json
		    			""");
  }
}
