package se.bjurr.wiremock.test.example_apis.model;

public class ItemDTO {
  private final String str;
  private int id;

  public ItemDTO(final String str) {
    this.str = str;
  }

  public ItemDTO setId(final int id) {
    this.id = id;
    return this;
  }

  public int getId() {
    return this.id;
  }

  public String getStr() {
    return this.str;
  }
}
