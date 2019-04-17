package se.bjurr.wiremock.test.integration.post;

public class StringDTO {
  private final String str;
  private Integer id;

  public StringDTO(final String str) {
    this.str = str;
  }

  public void setId(final Integer id) {
    this.id = id;
  }

  public Integer getId() {
    return id;
  }

  public String getStr() {
    return str;
  }
}
