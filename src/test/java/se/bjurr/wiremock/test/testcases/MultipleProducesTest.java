package se.bjurr.wiremock.test.testcases;

import static se.bjurr.wiremock.test.testutils.InvocationAssertion.assertThatApi;

import jakarta.ws.rs.core.MediaType;
import org.junit.Test;
import se.bjurr.wiremock.test.example_apis.MultipleProduces;
import se.bjurr.wiremock.test.example_apis.model.StringDTO;
import se.bjurr.wiremock.test.testutils.AcceptanceTestBase;

public class MultipleProducesTest extends AcceptanceTestBase {
  @Test
  public void json() {
    assertThatApi(MultipleProduces.class)
        .isInvokedLike((r) -> r.producesXmlAndJson())
        .withProducingMediaType(MediaType.APPLICATION_JSON)
        .andWillReturn(new StringDTO("whatever"))
        .shouldTranslateToMapping(
            """
{
  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
  "request" : {
    "urlPattern" : ".*/producesXmlOrJson$",
    "method" : "POST",
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
      "Content-Type" : "application/xml"
    }
  },
  "uuid" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"
}
""");
  }

  @Test
  public void xml() {
    assertThatApi(MultipleProduces.class)
        .isInvokedLike((r) -> r.producesXmlAndJson())
        .withProducingMediaType(MediaType.APPLICATION_XML)
        .andWillReturn(new StringDTO("whatever"))
        .shouldTranslateToMapping(
            """
{
  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
  "request" : {
    "urlPattern" : ".*/producesXmlOrJson$",
    "method" : "POST",
    "headers" : {
      "Accept" : {
        "equalTo" : "application/xml"
      }
    }
  },
  "response" : {
    "status" : 202,
    "body" : "<StringDTO>\\n  <str>whatever</str>\\n</StringDTO>\\n",
    "headers" : {
      "Content-Type" : "application/xml"
    }
  },
  "uuid" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"
}
""");
  }

  @Test
  public void octet() {
    assertThatApi(MultipleProduces.class)
        .isInvokedLike((r) -> r.producesXmlAndJson())
        .withProducingMediaType(MediaType.APPLICATION_OCTET_STREAM)
        .andWillReturn(new StringDTO("whatever"))
        .shouldThrow(
            """
		    			Was unable to determine media type. Tried to mock application/octet-stream but API has application/xml,application/json
		    			""");
  }

  @Test
  public void no_explicit_type_given() {
    assertThatApi(MultipleProduces.class)
        .isInvokedLike((r) -> r.producesXmlAndJson())
        .andWillReturn(new StringDTO("whatever"))
        .shouldThrow(
            """
		    			Was unable to determine media type. You need to explicitly set media type when there are several types in the API: application/xml,application/json
		    			""");
  }
}
