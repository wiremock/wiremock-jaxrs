package se.bjurr.wiremock.test.example_apis.resource_with_dto;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/")
public interface ItemResouce {

  @Path("/list")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<ItemDTO> getItems();

  @Path("/create")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public ItemDTO post(ItemDTO item);
}
