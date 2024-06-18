package strapi.cms.load.service;

import java.io.IOException;
import strapi.cms.load.bo.StrapiImagesBO;
import strapi.cms.load.bo.StrapiModulesBO;

public class ApplicationExecution {

  public static void main(String[] args) throws IOException {

    // ------------- CARGA IMÁGENES ---------------------------
    loadImages();

    // ------------- CARGA MÓDULOS ---------------------------
    loadModules();

  }

  public static void loadImages() throws IOException {
    System.out.println("Cargando imágenes procedentes de HOLA");
    StrapiImagesBO strapiImagesBO = new StrapiImagesBO();

    // Importa imagenes procedentes del Hola en los superproductos
    strapiImagesBO.importImagesFromHola();

    System.out.println("Carga y asociación de imágenes a productos finalizada");
  }

  public static void loadModules() {
    System.out.println("Creando módulos procedentes de HOLA");

    StrapiModulesBO strapiModulesBO = new StrapiModulesBO();
    boolean onlyEnabled = true;

    // Inicializa los modulos de todos los superproductos como desactivados
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

    System.out.println("Creación de módulos finalizada");
  }

}