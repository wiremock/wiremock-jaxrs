package se.bjurr.wiremock.test.integration.get;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface TestGetResouce {

  @Path("/getWithResponseObject")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public StringDTO getWithResponseObject();

  @Path("/getWithoutResponseObject")
  @GET
  public void getWithoutResponseObject();

  @Path("/getWithQueryParams")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public StringDTO getWithQueryParams(
      @QueryParam("oneparam") String param1,
      @QueryParam("secondparam") String param2,
      @QueryParam("thirdparam") String param3);

  @Path("/getWithPathParams/{oneparam}/{secondparam}/{thirdparam}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public StringDTO getWithPathParams(
      @PathParam("oneparam") String param1,
      @PathParam("secondparam") String param2,
      @PathParam("thirdparam") String param3);
}
