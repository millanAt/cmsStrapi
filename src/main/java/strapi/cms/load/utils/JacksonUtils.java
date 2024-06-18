package strapi.cms.load.utils;

import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Class of utilities for mapping objects to JSON and vice versa.
 * 
 * @author upe00901
 *
 */
public final class JacksonUtils {

  public static final String ERROR_MAPPING = "Error during JSON mapping: ";

  /**
   * Convierte un objeto a String JSON.
   * 
   * @param object objeto que se quiere convertir a JSON
   * @return objecto convertido en JSON
   */
  public static String toJson(Object object) {
    String response = null;
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    try {
      response = mapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
    }

    return response;
  }


  /**
   * Convierte un listado de JSON en formato String a un Array JSON.
   * 
   * @param objectList listado de objetos que se quiere convertir a array JSON
   * @return objecto convertido en array JSON
   */
  public static String jsonlistInStringFormatToJsonArray(List<String> objectList) {
    String response = null;
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    try {
      JsonNode jsonNode = mapper.readTree(objectList.toString());
      response = mapper.writeValueAsString(jsonNode);
    } catch (JsonProcessingException e) {

    }

    return response;
  }

  /**
   * Convierte un String JSON a un tipo de objeto dado
   * 
   * @param <T> tipo de objeto a parsear el JSON
   * @param jsonData json que se quiere mapear a objeto
   * @param objectType tipo de objeto dado
   * @return objecto generado
   */

  @SuppressWarnings("unchecked")
  public static <T> T toObject(String jsonData, Class<T> objectType) {
    ObjectMapper mapper = new ObjectMapper();
    T response = null;

    Class<T> valueType;
    try {
      valueType = (Class<T>) Class.forName(objectType.getName());
      response = mapper.readValue(jsonData, valueType);

    } catch (ClassNotFoundException | JsonProcessingException e) {

    }
    return response;
  }

  /**
   * Convierte un listado de String JSON a Lista de object
   * 
   * @param <T> tipo de objeto a parsear el JSON
   * @param jsonData json que se quiere mapear a objeto
   * @param objectType tipo de objeto dado
   * @return listdo de objectos mapeados
   */
  public static <T> List<T> toObjectListFromJsonArray(List<String> json, Class<T> objectType) {
    return toObjectList(json.toString(), objectType);
  }

  /**
   * Convierte un String JSON de tipo lista a Lista de object
   * 
   * @param <T> tipo de objeto a parsear el JSON
   * @param jsonData json que se quiere mapear a objeto
   * @param objectType tipo de objeto dado
   * @return listdo de objectos mapeados
   */
  @SuppressWarnings("unchecked")
  public static <T> List<T> toObjectList(String json, Class<T> objectType) {
    ObjectMapper mapper = new ObjectMapper();
    T[] responseList = null;

    try {
      Class<T[]> arrayClass = (Class<T[]>) Class.forName("[L" + objectType.getName() + ";");
      responseList = mapper.readValue(json, arrayClass);
    } catch (JsonProcessingException | ClassNotFoundException e) {
    }

    return Arrays.asList(responseList);
  }

}


