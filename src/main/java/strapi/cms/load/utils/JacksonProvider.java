package strapi.cms.load.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import javax.annotation.Priority;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * Clase que permite personalizar la configuración por defecto de la clase JacksonJsonProvider para
 * la serialización/deserialización de objetos.
 * 
 * @author upe00901
 *
 */
@Provider
@Priority(1)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JacksonProvider extends JacksonJsonProvider {
  private final ObjectMapper mapper;

  public JacksonProvider() {
    mapper = new ObjectMapper();
    // Configuramos el mapper para que no falle cuando se mapee campos no definidos o al
    // deserializar tipos primitivos que no vengan dados.
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);

    // Se configura el mapper para que en el caso de que llegue un enumerado no distinga entre
    // mayúsculas y minúsculas.
    mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    df.setTimeZone(TimeZone.getTimeZone("UTC"));
    mapper.setDateFormat(df);
    setMapper(mapper);
  }


}
