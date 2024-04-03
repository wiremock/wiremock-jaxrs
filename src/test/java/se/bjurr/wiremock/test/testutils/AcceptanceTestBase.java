package se.bjurr.wiremock.test.testutils;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.config.EncoderConfig.encoderConfig;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import java.io.File;
import java.util.Locale;
import java.util.UUID;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public class AcceptanceTestBase {

  protected static WireMockServer wireMockServer;

  @BeforeClass
  public static void setupServer() throws Exception {
    setupServerWithEmptyFileRoot();
    Locale.setDefault(Locale.ENGLISH);
    System.setProperty("line.separator", "\n");
  }

  @AfterClass
  public static void serverShutdown() {
    wireMockServer.stop();
  }

  public static void setupServerWithEmptyFileRoot() throws Exception {
    final String absolutePath =
        new File(AcceptanceTestBase.class.getResource("/empty").toURI()).getAbsolutePath();
    setupServer(wireMockConfig().withRootDirectory(absolutePath));
  }

  public static void setupServer(final WireMockConfiguration options) {
    options.dynamicPort();

    wireMockServer = new WireMockServer(options);
    wireMockServer.start();
    WireMock.configureFor(wireMockServer.port());
  }

  @Before
  public void init() throws InterruptedException {
    WireMock.resetToDefault();

    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    RestAssured.baseURI = "http://localhost:" + wireMockServer.port();
    final EncoderConfig encoderConfig =
        encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false);
    RestAssured.config = RestAssured.config().encoderConfig(encoderConfig);
  }

  public StubMapping setStaticUUIDs(final StubMapping sm) {
    sm.setUuid(UUID.fromString("d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"));
    sm.setId(UUID.fromString("d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"));
    return sm;
  }
}
