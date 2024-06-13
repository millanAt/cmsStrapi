package strapi.cms.images.utils;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

/**
 * Cliente Rest API que proporciona por defecto a Jackson como proveedor de contenido JSON.
 * 
 * @author upe00901
 *
 */
public class RestClient {


  /**
   * Devuelve el cliente Jersey.
   * 
   * @return cliente rest
   */
  public Client getClient() {
    return ClientBuilder.newBuilder().register(JacksonProvider.class).build();
  }


  /**
   * Devuelve el cliente Jersey al que se le añade la configuración de tiempo máximo de espera de
   * conexión y lectura
   * 
   * @param connectTimeout tiempo máximo de espera de conexión
   * @param readTimeout tiempo máximo de espera de lectura
   * @return cliente rest
   */
  public Client getClient(Integer connectTimeout, Integer readTimeout) {
    ClientBuilder clientBuilder =
        ClientBuilder.newBuilder().register(JacksonProvider.class).register(MultiPartFeature.class);
    clientBuilder.connectTimeout(Long.valueOf(connectTimeout), TimeUnit.SECONDS);
    clientBuilder.readTimeout(Long.valueOf(readTimeout), TimeUnit.SECONDS);

    return clientBuilder.build();
  }


  /**
   * Devuelve la cabecera de autorización básica a partir de un user y un pass.
   * 
   * @param user usuario de autenticación básica
   * @param pass contraseña usuario de autenticación básica
   * @return cabecera de autenticación básica
   */
  public String getAuthorizationBasic(String user, String pass) {
    String auth = user + ":" + pass;
    String encodedAuth =
        Base64.getEncoder().encodeToString(auth.getBytes(Charset.forName("ISO-8859-1")));
    String authHeader = "Basic " + encodedAuth;

    return authHeader;
  }
}
