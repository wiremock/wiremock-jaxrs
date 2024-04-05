package se.bjurr.wiremock.test.example_apis;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import se.bjurr.wiremock.test.example_apis.model.StringDTO;

@Path("/")
public interface ExampleApi {

  @Path("/getWithSingleProduces")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public StringDTO getWithSingleProduces();

  @Path("/getWithMultipleProduces")
  @GET
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  public StringDTO getWithMultipleProduces();
}
