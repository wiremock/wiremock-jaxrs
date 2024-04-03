package se.bjurr.wiremock.test.example_apis.resource_with_get_and_params;

public class StringDTO {
  private final String str;

  public StringDTO(final String str) {
    this.str = str;
  }

  public String getStr() {
    return str;
  }
}
