package strapi.cms.images.endpoint;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Clase de test de endpoint para entidades de clientes
 *
 */
@Path("/v2/strapi/test")
public class TransactionalTestEndPoint extends BaseEndPoint {



  @GET
  @Path("")
  @Produces(PRODUCES_MEDIA_TYPE)
  public Response transactionalTest() {

    return Response.ok("OK").build();
  }

}
