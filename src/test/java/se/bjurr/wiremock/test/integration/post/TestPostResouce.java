package se.bjurr.wiremock.test.integration.post;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface TestPostResouce {

  @Path("/createStringWithResponse")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public StringDTO createStringWithResponse(StringDTO dto);

  @Path("/createStringWithoutResponse")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public void createStringWithoutResponse(StringDTO dto);
}
