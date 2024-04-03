package se.bjurr.wiremock.test.example_apis.resource_with_dto;

public class ItemDTO {
  private final String str;
  private int id;

  public ItemDTO(final String str) {
    this.str = str;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public String getStr() {
    return str;
  }
}
