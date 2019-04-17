package se.bjurr.wiremock.example;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
