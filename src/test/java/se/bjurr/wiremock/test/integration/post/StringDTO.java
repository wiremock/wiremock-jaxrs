package se.bjurr.wiremock.test.integration.post;

import javax.xml.bind.annotation.XmlRootElement;

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
    return id;
  }

  public void setStr(String str) {
    this.str = str;
  }

  public String getStr() {
    return str;
  }
}
