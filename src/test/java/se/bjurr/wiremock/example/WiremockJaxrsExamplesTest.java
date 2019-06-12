package se.bjurr.wiremock.example;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMockJaxrs.invocation;
import static io.restassured.RestAssured.given;
import static javax.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.junit.Test;
import se.bjurr.wiremock.test.support.AcceptanceTestBase;

public class WiremockJaxrsExamplesTest extends AcceptanceTestBase {
  private static Logger LOG = Logger.getLogger(WiremockJaxrsExamplesTest.class.getSimpleName());

  @Test
  public void getItems() {
    final List<ItemDTO> responseObject = Arrays.asList(new ItemDTO("pong"));
    final StubMapping sm =
        stubFor( //
            invocation(ItemResouce.class, (r) -> r.getItems()) //
                .willReturn(aResponse().withStatus(SC_ACCEPTED), responseObject));

    final String actual = StubMapping.buildJsonStringFor(clean(sm));

    final String expected =
        "{\n"
            + "  \"id\" : \"d68fb4e2-48ed-40d2-bc73-0a18f54f3ece\",\n"
            + "  \"request\" : {\n"
            + "    \"urlPattern\" : \".*/list$\",\n"
            + "    \"method\" : \"GET\",\n"
            + "    \"headers\" : {\n"
            + "      \"Accept\" : {\n"
            + "        \"equalTo\" : \"application/json\"\n"
            + "      }\n"
            + "    }\n"
            + "  },\n"
            + "  \"response\" : {\n"
            + "    \"status\" : 202,\n"
            + "    \"body\" : \"[ {\\n  \\\"str\\\" : \\\"pong\\\",\\n  \\\"id\\\" : 0\\n} ]\",\n"
            + "    \"headers\" : {\n"
            + "      \"Content-Type\" : \"application/json\"\n"
            + "    }\n"
            + "  },\n"
            + "  \"uuid\" : \"d68fb4e2-48ed-40d2-bc73-0a18f54f3ece\"\n"
            + "}";

    assertThat(actual) //
        .isEqualTo(expected);

    LOG.info("\n" + expected);

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
    final ItemDTO responseObject = new ItemDTO("the item");
    responseObject.setId(123);
    final ItemDTO postedItem = new ItemDTO("the item");

    final StubMapping sm =
        stubFor( //
            invocation(ItemResouce.class, (r) -> r.post(postedItem)) //
                .willReturn(aResponse().withStatus(SC_ACCEPTED), responseObject));

    final String actual = StubMapping.buildJsonStringFor(clean(sm));

    final String expected =
        "{\n"
            + "  \"id\" : \"d68fb4e2-48ed-40d2-bc73-0a18f54f3ece\",\n"
            + "  \"request\" : {\n"
            + "    \"urlPattern\" : \".*/create$\",\n"
            + "    \"method\" : \"POST\",\n"
            + "    \"headers\" : {\n"
            + "      \"Content-Type\" : {\n"
            + "        \"equalTo\" : \"application/json\"\n"
            + "      },\n"
            + "      \"Accept\" : {\n"
            + "        \"equalTo\" : \"application/json\"\n"
            + "      }\n"
            + "    },\n"
            + "    \"bodyPatterns\" : [ {\n"
            + "      \"equalToJson\" : \"{\\n  \\\"str\\\" : \\\"the item\\\",\\n  \\\"id\\\" : 0\\n}\",\n"
            + "      \"ignoreArrayOrder\" : true,\n"
            + "      \"ignoreExtraElements\" : true\n"
            + "    } ]\n"
            + "  },\n"
            + "  \"response\" : {\n"
            + "    \"status\" : 202,\n"
            + "    \"body\" : \"{\\n  \\\"str\\\" : \\\"the item\\\",\\n  \\\"id\\\" : 123\\n}\",\n"
            + "    \"headers\" : {\n"
            + "      \"Content-Type\" : \"application/json\"\n"
            + "    }\n"
            + "  },\n"
            + "  \"uuid\" : \"d68fb4e2-48ed-40d2-bc73-0a18f54f3ece\"\n"
            + "}";

    assertThat(actual) //
        .isEqualTo(expected);

    LOG.info("\n" + expected);

    given() //
        .accept("application/json") //
        .contentType("application/json") //
        .request() //
        .body(postedItem) //
        .post("/create") //
        .then()
        .assertThat() //
        .contentType("application/json") //
        .statusCode(SC_ACCEPTED);
  }

  private StubMapping clean(final StubMapping sm) {
    sm.setUuid(UUID.fromString("d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"));
    sm.setId(UUID.fromString("d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"));
    return sm;
  }
}
