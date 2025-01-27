package strapi.cms.load.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import strapi.cms.load.dto.DataRequestDTO;
import strapi.cms.load.dto.UploadImageResultDTO;
import strapi.cms.load.utils.Constants;
import strapi.cms.load.utils.RestClient;
import strapi.cms.loads.enums.LocaleStrapi;

@Stateless
@Named("StrapiRequester")
public class StrapiRequesterService extends RestClient {

  public Client getRestClient() {
    Integer connectTimeout = 5;
    Integer readTimeout = 15;

    Client client = getClient(connectTimeout, readTimeout);
    return client;
  }

  public UploadImageResultDTO uploadImage(File file) {

    // log.info("StrapiRequesterService - upload image to Strapi");

    List<UploadImageResultDTO> resultList = new ArrayList<>();

    String token = String.format("Bearer %s", System.getenv("TOKEN"));

    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    headers.add("Authorization", token);

    String environment = System.getenv("ENVIRONMENT");
    String baseURL =
        environment.equalsIgnoreCase("pro") ? Constants.BASE_URL_PRO : Constants.BASE_URL_PRE;

    String imagePath = "/api/upload";
    Client client = getRestClient();

    try {

      FileDataBodyPart filePart = new FileDataBodyPart("files", file);

      FormDataMultiPart multiPart = new FormDataMultiPart();
      multiPart.bodyPart(filePart);


      Response response = client.target(baseURL + imagePath).request(MediaType.MULTIPART_FORM_DATA)
          .headers(headers).post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA));

      if (response.getStatus() == 201) {
        resultList = response.readEntity(new GenericType<List<UploadImageResultDTO>>() {});
      }

    } catch (Exception e) {
      System.out.println(
          "Ha fallado la subida de la imagen: " + file.getName() + ". Error: " + e.getMessage());
    }

    client.close();
    return !resultList.isEmpty() ? resultList.get(0) : null;

  }

  public boolean publishElement(String entity, String documentId, LocaleStrapi locale) {

    System.out.println(String.format(
        "StrapiRequesterService - Publishing the content of the element: %s belonging to the entity: %s and language: %s",
        documentId, entity, locale));

    boolean isSuccess = false;

    String token = String.format("Bearer %s", System.getenv("TOKEN"));

    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    headers.add("Authorization", token);

    String environment = System.getenv("ENVIRONMENT");
    String baseURL =
        environment.equalsIgnoreCase("pro") ? Constants.BASE_URL_PRO : Constants.BASE_URL_PRE;

    String urlPath = String.format("/api/%s/%s?locale=%s", entity, documentId, locale.getLabel());
    Client client = getRestClient();

    try {

      DataRequestDTO dataRequestDto = new DataRequestDTO(new Date());

      Response response = client.target(baseURL + urlPath).request().headers(headers)
          .put(Entity.entity(dataRequestDto, MediaType.APPLICATION_JSON));

      if (response.getStatus() == 200) {
        System.out.println(String.format(
            "Se ha publicado con éxito la entidad %s con documentId: %s", entity, documentId));
        isSuccess = true;
      } else {
        System.out.println(String.format(
            "No se ha podido publicar la entidad %s con documentId: %s", entity, documentId));
      }

    } catch (Exception e) {
      System.out.println(
          "Ha fallado la publicación de la entidad " + entity + ". Error: " + e.getMessage());
    }

    client.close();
    return isSuccess;
  }

}
