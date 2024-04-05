package se.bjurr.wiremock.test.example_apis.model;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CommonDTO")
public class CommonDTO {
  private String str;

  public CommonDTO() {}

  public CommonDTO(final String str) {
    this.str = str;
  }

  public void setStr(final String str) {
    this.str = str;
  }

  public String getStr() {
    return this.str;
  }
}
