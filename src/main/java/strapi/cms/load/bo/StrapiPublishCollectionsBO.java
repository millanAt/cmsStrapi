package strapi.cms.load.bo;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import strapi.cms.load.service.StrapiRequesterService;
import strapi.cms.load.utils.Constants;
import strapi.cms.loads.enums.LocaleStrapi;

@Stateless
public class StrapiPublishCollectionsBO {

  private EntityManagerFactory emf;
  private EntityManager entityManager;


  public StrapiPublishCollectionsBO(EntityManagerFactory emf) {
    super();
    this.emf = emf;
  }

  /**
   * Elimina las entidades publicadas
   */
  public void initializePublishCollections() {
    entityManager = emf.createEntityManager();
    entityManager.getTransaction().begin();

    System.out.println(
        "----------  Eliminando todas las publicaciones de colecciones en Strapi ----------");

    Query query = entityManager
        .createNativeQuery("DELETE FROM CMSSTRAPI.channels where published_at is not null");
    query.executeUpdate();
    query = entityManager
        .createNativeQuery("DELETE FROM CMSSTRAPI.countries where published_at is not null");
    query.executeUpdate();
    query = entityManager.createNativeQuery(
        "DELETE FROM CMSSTRAPI.module_accordions where published_at is not null");
    query.executeUpdate();
    query = entityManager
        .createNativeQuery("DELETE FROM CMSSTRAPI.module_boxes where published_at is not null");
    query.executeUpdate();
    query = entityManager.createNativeQuery(
        "DELETE FROM CMSSTRAPI.module_link_downloads where published_at is not null");
    query.executeUpdate();
    query = entityManager.createNativeQuery(
        "DELETE FROM CMSSTRAPI.op_verificadas_configs where published_at is not null");
    query.executeUpdate();
    query = entityManager
        .createNativeQuery("DELETE FROM CMSSTRAPI.products where published_at is not null");
    query.executeUpdate();
    query = entityManager
        .createNativeQuery("DELETE FROM CMSSTRAPI.promos where published_at is not null");
    query.executeUpdate();
    query = entityManager
        .createNativeQuery("DELETE FROM CMSSTRAPI.super_products where published_at is not null");
    query.executeUpdate();
    query = entityManager.createNativeQuery(
        "DELETE FROM CMSSTRAPI.super_products_groups where published_at is not null");
    query.executeUpdate();
    query = entityManager.createNativeQuery(
        "DELETE FROM CMSSTRAPI.super_product_group_mis where published_at is not null");
    query.executeUpdate();
    query = entityManager.createNativeQuery(
        "DELETE FROM CMSSTRAPI.super_product_channels where published_at is not null");
    query.executeUpdate();
    query = entityManager.createNativeQuery(
        "DELETE FROM CMSSTRAPI.related_super_products where published_at is not null");
    query.executeUpdate();
    query = entityManager
        .createNativeQuery("DELETE FROM CMSSTRAPI.group_entities where published_at is not null");
    query.executeUpdate();
    query = entityManager
        .createNativeQuery("DELETE FROM CMSSTRAPI.variants where published_at is not null");
    query.executeUpdate();
    query = entityManager
        .createNativeQuery("DELETE FROM CMSSTRAPI.vouchers where published_at is not null");
    query.executeUpdate();
    query = entityManager
        .createNativeQuery("DELETE FROM CMSSTRAPI.widget_types where published_at is not null");
    query.executeUpdate();
    query = entityManager
        .createNativeQuery("DELETE FROM CMSSTRAPI.widgets where published_at is not null");
    query.executeUpdate();
    query = entityManager
        .createNativeQuery("DELETE FROM CMSSTRAPI.widget_pages where published_at is not null");
    query.executeUpdate();

    entityManager.getTransaction().commit();


    entityManager.close();

  }


  /**
   * Publica todas las collecciones en Strapi
   */
  @SuppressWarnings("unchecked")
  public void publishCollections() {

    entityManager = emf.createEntityManager();
    StrapiRequesterService strapiRequest = new StrapiRequesterService();

    String[] collections = Constants.STRAPI_COLLECTIONS.split(",");

    for (String collection : collections) {

      System.out
          .println("----------  Publicando Colección " + collection + " en Strapi ----------");

      if (Constants.STRAPI_GROUP_ENTITIES.equals(collection)) {

        String placeholders = String.join(",",
            Constants.STRAPI_SP_GROUP_LIST.stream().map(value -> "'" + value + "'").toList());

        // Publicamos los groupEnitites que tengan solo asociados super_productos
        Query query = entityManager.createNativeQuery("SELECT DISTINCT document_id FROM CMSSTRAPI."
            + collection + " WHERE title NOT IN (" + placeholders + ")");
        this.publishElementInStrapi(strapiRequest, collection, query.getResultList());

        // Publicamos los superProductGroups que tengan asociados groupEntities con super_productos
        query = entityManager.createNativeQuery("SELECT DISTINCT document_id FROM CMSSTRAPI."
            + Constants.STRAPI_SP_GROUP_ENTITY + " WHERE code IN (" + placeholders + ")");
        this.publishElementInStrapi(strapiRequest, Constants.STRAPI_SP_GROUP_ENTITY,
            query.getResultList());

        // Publicamos los groupEnitites que tengan asociados los super_products_groups que no hemos
        // incluido antes
        query = entityManager.createNativeQuery("SELECT DISTINCT document_id FROM CMSSTRAPI."
            + collection + " WHERE title IN (" + placeholders + ")");
        this.publishElementInStrapi(strapiRequest, collection, query.getResultList());

        // Publicamos el resto de superProductGroups que no hemos publicado antes
        query = entityManager.createNativeQuery("SELECT DISTINCT document_id FROM CMSSTRAPI."
            + Constants.STRAPI_SP_GROUP_ENTITY + " WHERE code NOT IN (" + placeholders + ")");
        this.publishElementInStrapi(strapiRequest, Constants.STRAPI_SP_GROUP_ENTITY,
            query.getResultList());

      } else {

        // Obtiene el listado de tipos de widgets
        Query query = entityManager
            .createNativeQuery("SELECT DISTINCT document_id FROM CMSSTRAPI." + collection);

        this.publishElementInStrapi(strapiRequest, collection, query.getResultList());
      }

    }

    entityManager.close();

    System.out.println("Se han publicado todas las colecciones en Strapi");

  }

  private void publishElementInStrapi(StrapiRequesterService strapiRequest, String collection,
      List<String> documentIdList) {

    if (documentIdList != null) {

      // Iteramos sobre cada una de los identificadores de cada colección
      for (String documentId : documentIdList) {

        // Creamos una publicación por cada idioma
        for (LocaleStrapi locale : LocaleStrapi.getValues()) {
          strapiRequest.publishElement(collection.replaceAll("_", "-"), documentId, locale);
        }
      }
    }
  }

}

