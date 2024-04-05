package se.bjurr.wiremock.test.testcases;

import static se.bjurr.wiremock.test.testutils.InvocationAssertion.assertThatApi;

import org.junit.Test;
import se.bjurr.wiremock.test.example_apis.ConsumesAndProduces;
import se.bjurr.wiremock.test.example_apis.model.CommonDTO;
import se.bjurr.wiremock.test.example_apis.model.StringDTO;
import se.bjurr.wiremock.test.testutils.AcceptanceTestBase;

public class ConsumesAndProducesTest extends AcceptanceTestBase {
  @Test
  public void consumesAndProducesNothing() {
    assertThatApi(ConsumesAndProduces.class)
        .isInvokedLike((r) -> r.consumesAndProducesNothing())
        .shouldTranslateToMapping(
            """
{
  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
  "request" : {
    "urlPattern" : ".*/consumesAndProducesNothing$",
    "method" : "POST"
  },
  "response" : {
    "status" : 202
  },
  "uuid" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"
}
""");

    assertThatApi(ConsumesAndProduces.class)
        .isInvokedLike((r) -> r.consumesAndProducesNothing())
        .andWillReturn(new CommonDTO("pong"))
        .shouldThrow(
            """
			Cannot assign void from class se.bjurr.wiremock.test.example_apis.model.CommonDTO
			""");
  }

  @Test
  public void consumesAndProducesXml() {
    assertThatApi(ConsumesAndProduces.class)
        .isInvokedLike((r) -> r.consumesXmlAndProducesJson(new StringDTO("s")))
        .andWillReturn(new StringDTO("pong"))
        .shouldTranslateToMapping(
            """
{
  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
  "request" : {
    "urlPattern" : ".*/consumesAndProducesXml$",
    "method" : "POST",
    "headers" : {
      "Content-Type" : {
        "equalTo" : "application/xml"
      },
      "Accept" : {
        "equalTo" : "application/json"
      }
    },
    "bodyPatterns" : [ {
      "equalToXml" : "<StringDTO>\\n  <str>s</str>\\n</StringDTO>\\n"
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

    assertThatApi(ConsumesAndProduces.class)
        .isInvokedLike((r) -> r.consumesXmlAndProducesJson(new StringDTO("s")))
        .andWillReturn(new CommonDTO("pong"))
        .shouldThrow(
            """
    			Cannot assign class se.bjurr.wiremock.test.example_apis.model.StringDTO from class se.bjurr.wiremock.test.example_apis.model.CommonDTO
    			""");
  }

  @Test
  public void consumesXml() {
    assertThatApi(ConsumesAndProduces.class)
        .isInvokedLike((r) -> r.consumesXml(new StringDTO("s")))
        .shouldTranslateToMapping(
            """
{
  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
  "request" : {
    "urlPattern" : ".*/consumesXml$",
    "method" : "POST",
    "headers" : {
      "Content-Type" : {
        "equalTo" : "application/xml"
      }
    },
    "bodyPatterns" : [ {
      "equalToXml" : "<StringDTO>\\n  <str>s</str>\\n</StringDTO>\\n"
    } ]
  },
  "response" : {
    "status" : 202
  },
  "uuid" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"
}
""");

    assertThatApi(ConsumesAndProduces.class)
        .isInvokedLike((r) -> r.consumesXml(new StringDTO("s")))
        .andWillReturn(new CommonDTO("pong"))
        .shouldThrow(
            """
    			Cannot assign void from class se.bjurr.wiremock.test.example_apis.model.CommonDTO
    			""");
  }

  @Test
  public void producesXml() {
    assertThatApi(ConsumesAndProduces.class)
        .isInvokedLike((r) -> r.producesXml())
        .andWillReturn(new StringDTO("pong"))
        .shouldTranslateToMapping(
            """
{
  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
  "request" : {
    "urlPattern" : ".*/producesXml$",
    "method" : "POST",
    "headers" : {
      "Accept" : {
        "equalTo" : "application/xml"
      }
    }
  },
  "response" : {
    "status" : 202,
    "body" : "<StringDTO>\\n  <str>pong</str>\\n</StringDTO>\\n",
    "headers" : {
      "Content-Type" : "application/xml"
    }
  },
  "uuid" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"
}
""");

    assertThatApi(ConsumesAndProduces.class)
        .isInvokedLike((r) -> r.producesXml())
        .andWillReturn(new CommonDTO("pong"))
        .shouldThrow(
            """
    			Cannot assign class se.bjurr.wiremock.test.example_apis.model.StringDTO from class se.bjurr.wiremock.test.example_apis.model.CommonDTO
    			""");
  }
}
