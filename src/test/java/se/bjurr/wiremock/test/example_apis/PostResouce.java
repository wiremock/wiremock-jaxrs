package se.bjurr.wiremock.test.example_apis;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import se.bjurr.wiremock.test.example_apis.model.StringDTO;

@Path("/")
public interface PostResouce {

  @Path("/createStringWithResponse")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public StringDTO createStringWithResponse(StringDTO dto);

  @Path("/createStringWithoutResponse")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public void createStringWithoutResponse(StringDTO dto);

  @Path("/createXmlStringWithoutResponse")
  @POST
  @Consumes(MediaType.APPLICATION_XML)
  public void createXmlStringWithoutResponse(StringDTO dto);
}
