package strapi.cms.load.service;

import java.io.IOException;
import javax.persistence.EntityManagerFactory;
import strapi.cms.load.bo.StrapiImagesBO;
import strapi.cms.load.bo.StrapiModulesBO;
import strapi.cms.load.bo.StrapiPublishCollectionsBO;
import strapi.cms.load.bo.StrapiWidgetBO;
import strapi.cms.load.utils.EntityManagerUtil;

public class ApplicationExecution {

  public static void main(String[] args) throws IOException {

    // Obtener los valores de dbUrl, username y password de las variables de entorno
    String dbUrl = System.getenv("DB_URL");
    String username = System.getenv("DB_USER");
    String password = System.getenv("DB_PASS");

    if (dbUrl == null || username == null || password == null) {
      System.out.println(
          "No se puede realizar la carga de imágenes, módulos y widgets en Strapi porque no se han definido las variables de entorno");
    } else {

      EntityManagerFactory emf =
          EntityManagerUtil.getEntityManagerFactory(dbUrl, username, password);

      // ------------- CARGA IMÁGENES ---------------------------
      loadImages(emf);

      // ------------- CARGA MÓDULOS ---------------------------
      loadModules(emf);

      // ------------- CARGA WIDGETS ---------------------------
      loadWidgets(emf);

      // ---------------- PUBLICAR COLECCIONES -------------------------
      publishCollections(emf);
    }
  }

  public static void loadImages(EntityManagerFactory emf) throws IOException {
    System.out.println("//////////// Cargando imágenes procedentes de HOLA ////////////");
    StrapiImagesBO strapiImagesBO = new StrapiImagesBO(emf);

    // Inicializa todas las imagenes y las relaciones con los superproductos.
    strapiImagesBO.initializeImages();

    // Importa imagenes procedentes del Hola en los superproductos
    strapiImagesBO.importImagesFromHola();

    System.out.println(
        "----------------- Carga y asociación de imágenes a productos finalizada -----------------");
  }

  public static void loadModules(EntityManagerFactory emf) {
    System.out.println("//////////// Creando módulos procedentes de HOLA ////////////");

    StrapiModulesBO strapiModulesBO = new StrapiModulesBO(emf);
    boolean onlyEnabled = true;

    // Inicializa los modulos de todos los superproductos
    strapiModulesBO.initializeModules();

    // Creación de módulos Box y asociación a super productos
    strapiModulesBO.createBoxModuleAndAssociateToProduct(onlyEnabled);

    // Creación de módulos Link Download y asociación a super productos
    strapiModulesBO.createLinkDownloadModuleAndAssociateToProduct(onlyEnabled);

    // Creación de módulos Accordion y asociación a super productos
    strapiModulesBO.createAccordionModuleAndAssociateToProduct(onlyEnabled);

    // Activar/desactivar módulo slider en super productos
    strapiModulesBO.activateOrDesactivateSliderModuleOnProduct(onlyEnabled);

    // Activar/desactivar módulo corporacion logo en super productos
    strapiModulesBO.activateOrDesactivateLogoModuleOnProduct(onlyEnabled);

    System.out.println("----------------- Creación de módulos finalizada -----------------");
  }

  public static void loadWidgets(EntityManagerFactory emf) {
    System.out.println("//////////// Generación de widgets procedentes de HOLA ////////////");
    StrapiWidgetBO strapiWidgetBO = new StrapiWidgetBO(emf);

    // Inicializa los widget
    strapiWidgetBO.initializeWidgets();

    // Genera los tipos de widgets procedentes de HOLA
    strapiWidgetBO.createWidgetType();

    // Genera los widgets procedentes de HOLA
    strapiWidgetBO.createWidgets();

    // Genera los widget pages procedentes de HOLA
    strapiWidgetBO.createWidgetPages();

    System.out.println("----------------- Creación de widgets finalizada -----------------");
  }

  public static void publishCollections(EntityManagerFactory emf) throws IOException {
    System.out.println("//////////// Publicando colecciones ////////////");
    StrapiPublishCollectionsBO strapiPublishCollectionsBO = new StrapiPublishCollectionsBO(emf);

    // Elimina publicaciones de todas las colecciones
    strapiPublishCollectionsBO.initializePublishCollections();

    // Publica todas las colecciones en Strapi
    strapiPublishCollectionsBO.publishCollections();

    System.out.println("----------------- Publicación de colecciones finalizada -----------------");
  }

}
