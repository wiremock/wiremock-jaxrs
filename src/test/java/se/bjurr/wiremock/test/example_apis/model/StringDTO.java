package se.bjurr.wiremock.test.example_apis.model;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "StringDTO")
public class StringDTO {
  private String str;
  private Integer id;

  public StringDTO() {
    this(null);
  }

  public StringDTO(final String str) {
    this.str = str;
  }

  public void setId(final Integer id) {
    this.id = id;
  }

  public Integer getId() {
    return this.id;
  }

  public void setStr(final String str) {
    this.str = str;
  }

  public String getStr() {
    return this.str;
  }
}
