package se.bjurr.wiremock.test.integration.get;

public class StringDTO {
  private final String str;

  public StringDTO(final String str) {
    this.str = str;
  }

  public String getStr() {
    return str;
  }
}
