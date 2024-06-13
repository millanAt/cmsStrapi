package strapi.cms.images.service;

import java.io.File;
import java.util.ArrayList;
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
import strapi.cms.images.dto.UploadImageResultDTO;
import strapi.cms.images.utils.RestClient;

@Stateless
@Named("StrapiRequester")
public class StrapiRequesterService extends RestClient {


  // @Inject
  // private Logger log;

  private static String TOKEN =
      "Bearer a64101d86de6b2fb3d6b97be040025d2d90bd79c71fc337eed34327e7fbb864da0c7070422910b85f05669c488ed7b4974550b99bab5482698ef2d08a9a44209d55b1b44c7517bb74a904b17bb63319bfc64b1771bf74365e7e26f19f57c45d40e68905e311ec3baf3e1a3ef42890ed8146bd72a243650bea75b40afbe7f4013";

  // START - MODULE PARAMETRE_TECNIC

  public Client getRestClient() {
    Integer connectTimeout = 5;
    Integer readTimeout = 15;
    // log.debug("create http client for TMBC connectTimeout = {}, readTimeout = {}",
    // connectTimeout,
    // readTimeout);

    Client client = getClient(connectTimeout, readTimeout);
    return client;
  }

  public UploadImageResultDTO uploadImage(File file) {

    // log.info("StrapiRequesterService - upload image to Strapi");

    List<UploadImageResultDTO> resultList = new ArrayList<>();

    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    headers.add("Authorization", TOKEN);

    String baseUrl = "https://pre.c1.cloud-2.tmb.cat/cms";
    String imagePath = "/api/upload";
    Client client = getRestClient();

    try {

      // File file = new File(fileName);
      FileDataBodyPart filePart = new FileDataBodyPart("files", file);

      FormDataMultiPart multiPart = new FormDataMultiPart();
      multiPart.bodyPart(filePart);


      Response response = client.target(baseUrl + imagePath).request(MediaType.MULTIPART_FORM_DATA)
          .headers(headers).post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA));

      if (response.getStatus() == 200) {
        resultList = response.readEntity(new GenericType<List<UploadImageResultDTO>>() {});
      }

    } catch (Exception e) {
      // log.error("ERROR");
      System.out.println(e.getMessage());
    }

    client.close();
    return !resultList.isEmpty() ? resultList.get(0) : null;

  }

}
