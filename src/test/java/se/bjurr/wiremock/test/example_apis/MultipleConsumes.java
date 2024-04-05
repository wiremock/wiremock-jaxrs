package se.bjurr.wiremock.test.example_apis;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import se.bjurr.wiremock.test.example_apis.model.StringDTO;

@Path("/")
public interface MultipleConsumes {

  @Path("/consumesXmlOrJson")
  @POST
  @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  public void consumesXmlAndJson(StringDTO dto);
}
