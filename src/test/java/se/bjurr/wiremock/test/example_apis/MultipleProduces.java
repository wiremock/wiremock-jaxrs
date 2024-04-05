package se.bjurr.wiremock.test.example_apis;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import se.bjurr.wiremock.test.example_apis.model.StringDTO;

@Path("/")
public interface MultipleProduces {

  @Path("/producesXmlOrJson")
  @POST
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  public StringDTO producesXmlAndJson();
}
