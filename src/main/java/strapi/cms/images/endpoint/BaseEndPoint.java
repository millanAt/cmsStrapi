package strapi.cms.images.endpoint;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Base class for all End Points.
 */
public abstract class BaseEndPoint {

  public static final String PRODUCES_MEDIA_TYPE = MediaType.APPLICATION_JSON + ";charset=UTF-8";


  @PostConstruct
  private void postConstruct() {}


  public Response buildNotFoundResponse(String errorCode) {
    return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON_TYPE).build();
  }

  public Response buildErrorResponse(String errorCode) {
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .type(MediaType.APPLICATION_JSON_TYPE).build();
  }

  public <T> Response buildErrorResponse(T entity) {
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(entity)
        .type(MediaType.APPLICATION_JSON_TYPE).build();
  }

  public <T> Response buildOKResponse(T entity) {
    return Response.ok().entity(entity).type(MediaType.APPLICATION_JSON_TYPE)
        // en este punto no controlamos CORS lo dejamos para el filtro
        // .header("Access-Control-Allow-Origin", "*")
        .build();
  }

  public <T> Response buildOKResponseHTML(T entity) {
    return Response.ok().entity(entity).type(MediaType.TEXT_HTML).build();
  }


}
