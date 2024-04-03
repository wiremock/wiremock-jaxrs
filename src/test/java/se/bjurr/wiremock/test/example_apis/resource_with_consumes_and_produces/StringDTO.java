package se.bjurr.wiremock.test.example_apis.resource_with_consumes_and_produces;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "StringDTO")
public class StringDTO {
  private String str;

  public StringDTO() {}

  public StringDTO(final String str) {
    this.str = str;
  }

  public void setStr(final String str) {
    this.str = str;
  }

  public String getStr() {
    return this.str;
  }
}
