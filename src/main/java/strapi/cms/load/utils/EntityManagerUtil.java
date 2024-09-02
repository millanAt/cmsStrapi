package strapi.cms.load.utils;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerUtil {

  private static EntityManagerFactory emf;


  public static EntityManagerFactory getEntityManagerFactory(String dbUrl, String username,
      String password) {
    if (emf == null) {
      emf = crearEntityManagerFactoryWithParams(dbUrl, username, password);
    }
    return emf;
  }

  public static EntityManagerFactory crearEntityManagerFactoryWithParams(String dbUrl,
      String username, String password) {

    Map<String, String> propiedades = new HashMap<>();

    // Sobreescribimos los valores de conexión
    // propiedades.put("javax.persistence.jdbc.url", dbUrl);
    // propiedades.put("javax.persistence.jdbc.user", username);
    // propiedades.put("javax.persistence.jdbc.password", password);

    propiedades.put("hibernate.connection.url", dbUrl);
    propiedades.put("hibernate.connection.username", username);
    propiedades.put("hibernate.connection.password", password);

    // Crear el EntityManagerFactory con las propiedades dinámicas
    return Persistence.createEntityManagerFactory("mysqlPU1", propiedades);
  }

  // Método para cerrar el EntityManagerFactory cuando se cierre la aplicación
  public static void cerrarEntityManagerFactory() {
    if (emf != null && emf.isOpen()) {
      emf.close();
    }
  }

}
