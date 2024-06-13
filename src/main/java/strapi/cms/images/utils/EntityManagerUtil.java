package strapi.cms.images.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerUtil {
  private static final EntityManagerFactory mysqlEmf =
      Persistence.createEntityManagerFactory("mysqlPU1");

  public static EntityManager getMySQLEntityManager() {
    return mysqlEmf.createEntityManager();
  }

}
