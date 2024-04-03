package se.bjurr.wiremock.test.example_apis.resource_with_consumes_and_produces;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
public interface ConsumesAndProduces {

  @Path("/consumesAndProducesNothing")
  @POST
  public void consumesAndProducesNothing();

  @Path("/consumesXml")
  @POST
  @Consumes(MediaType.APPLICATION_XML)
  public void consumesXml(StringDTO dto);

  @Path("/producesXml")
  @POST
  @Produces(MediaType.APPLICATION_XML)
  public StringDTO producesXml();

  @Path("/consumesAndProducesXml")
  @POST
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_JSON)
  public StringDTO consumesXmlAndProducesJson(StringDTO dto);
}
