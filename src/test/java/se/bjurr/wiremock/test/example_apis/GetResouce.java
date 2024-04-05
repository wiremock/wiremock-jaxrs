package se.bjurr.wiremock.test.example_apis;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import se.bjurr.wiremock.test.example_apis.model.StringDTO;

@Path("/")
public interface GetResouce {

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
