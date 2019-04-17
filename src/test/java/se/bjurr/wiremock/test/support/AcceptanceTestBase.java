package se.bjurr.wiremock.test.support;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.jayway.restassured.config.EncoderConfig.encoderConfig;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.EncoderConfig;
import java.io.File;
import java.util.Locale;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public class AcceptanceTestBase {

  protected static WireMockServer wireMockServer;

  @BeforeClass
  public static void setupServer() throws Exception {
    setupServerWithEmptyFileRoot();
    Locale.setDefault(Locale.ENGLISH);
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
}
