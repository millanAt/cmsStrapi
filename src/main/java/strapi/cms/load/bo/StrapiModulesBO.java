package strapi.cms.load.bo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import com.mysql.cj.util.StringUtils;
import strapi.cms.load.dto.ModuleAccordionDTO;
import strapi.cms.load.dto.ModuleBoxDTO;
import strapi.cms.load.dto.ModuleLinkDownloadDTO;
import strapi.cms.load.dto.ModuleSliderDTO;
import strapi.cms.load.utils.EntityManagerUtil;
import strapi.cms.load.utils.JacksonUtils;
import strapi.cms.loads.enums.ImageField;
import strapi.cms.loads.enums.ImageRelatedType;
import strapi.cms.loads.enums.LocaleStrapi;
import strapi.cms.loads.enums.ModuleType;

@Stateless
public class StrapiModulesBO {

  private EntityManager entityManager;

  /**
   * Crea un modulo link download por cada uno de los tabs asociados a un producto (un modulo por
   * cada idioma de producto).
   */
  @SuppressWarnings("rawtypes")
  public void createLinkDownloadModuleAndAssociateToProduct(boolean onlyEnabled) {

    // Obtener el listado de superProductos (id de Strapi) y configuraciones del módulo LINK
    // DOWNLOAD
    List<Object[]> queryResultList =
        this.getModuleConfigFromHola(ModuleType.LINK_DOWNLOAD, onlyEnabled);


    if (!queryResultList.isEmpty()) {

      entityManager.getTransaction().begin();

      // Iteramos sobre cada una de los superproductos
      for (Iterator iterator = queryResultList.iterator(); iterator.hasNext();) {
        Object[] row = (Object[]) iterator.next();

        String productCode = (String) row[0];
        String config = (String) row[1];
        Boolean enabled = (Boolean) row[2];


        System.out.println(String.format(
            "Creando módulo Link Download y asociándolo al super producto: %s", productCode));

        // Se añade en STRAPI la configuración del módulo BOX para el producto determinado
        if (config != null) {

          ModuleLinkDownloadDTO moduleLinkDownDto =
              JacksonUtils.toObject(config, ModuleLinkDownloadDTO.class);
          List<Long> moduleLinkList = new ArrayList<>();

          // Creamos una entrada en el módulo de Link Download para cada uno de los idiomas dados
          for (LocaleStrapi locale : LocaleStrapi.getValues()) {

            Integer superProductId = null;
            try {
              // Obtenemos el superproducto asociado al código de producto y en el idioma dado
              Query query = entityManager.createNativeQuery(
                  "SELECT id from CMSSTRAPI.super_products where code = ? and locale = ?");
              query.setParameter(1, productCode);
              query.setParameter(2, locale.getLabel());
              superProductId = (Integer) query.getSingleResult();
            } catch (NoResultException e) {
              System.out.println(String
                  .format("No se encontró en Strapi el producto con código: %s", productCode));
            }


            if (superProductId != null) {
              // Se actualiza el superproducto activando/desactivando el módulo link download según
              // venga dado
              Query query = entityManager.createNativeQuery(
                  "UPDATE CMSSTRAPI.super_products SET module_link_download= ? WHERE id=?");
              query.setParameter(1, enabled);
              query.setParameter(2, superProductId);
              query.executeUpdate();

              // Genera los diferetes modulos link download para cada uno de los productos
              generateModuleLinkDownloadFromConfig(moduleLinkDownDto, moduleLinkList, locale,
                  superProductId);
            }

          }

          // Relaciona módulos de idiomas
          relatedLanguageModules(moduleLinkList, ModuleType.LINK_DOWNLOAD);

        }

      }
      entityManager.getTransaction().commit();

    }

    entityManager.close();
  }

  /**
   * Crea un modulo box por cada uno de los tabs asociados a un producto (un modulo por cada tab y
   * cada idioma de producto).
   */
  @SuppressWarnings("rawtypes")
  public void createBoxModuleAndAssociateToProduct(boolean onlyEnabled) {

    // Obtener el listado de superProductos (id de Strapi) y configuraciones del módulo BOX
    List<Object[]> queryResultList = this.getModuleConfigFromHola(ModuleType.BOX, onlyEnabled);

    if (!queryResultList.isEmpty()) {

      entityManager.getTransaction().begin();

      // Iteramos sobre cada una de los superproductos
      for (Iterator iterator = queryResultList.iterator(); iterator.hasNext();) {
        Object[] row = (Object[]) iterator.next();

        String productCode = (String) row[0];
        String config = (String) row[1];
        Boolean enabled = (Boolean) row[2];

        System.out.println(
            String.format("Creando módulo Box y asociándolo al super producto: %s", productCode));

        // Se añade en STRAPI la configuración del módulo BOX para el producto determinado
        if (config != null) {

          ModuleBoxDTO moduleBoxDto = JacksonUtils.toObject(config, ModuleBoxDTO.class);
          Map<Integer, List<Long>> moduleBoxList = new HashMap<>();

          // Creamos una entrada en el módulo de BOX para cada uno de los idiomas dados
          for (LocaleStrapi locale : LocaleStrapi.getValues()) {

            Integer superProductId = null;
            try {
              // Obtenemos el superproducto asociado al código de producto y en el idioma dado
              Query query = entityManager.createNativeQuery(
                  "SELECT id from CMSSTRAPI.super_products where code = ? and locale = ?");
              query.setParameter(1, productCode);
              query.setParameter(2, locale.getLabel());
              superProductId = (Integer) query.getSingleResult();
            } catch (NoResultException e) {
              System.out.println(String
                  .format("No se encontró en Strapi el producto con código: %s", productCode));
            }


            if (superProductId != null) {

              // Se actualiza el superproducto con el título del modulo box
              Query query = entityManager.createNativeQuery(
                  "UPDATE CMSSTRAPI.super_products SET module_box_title= ?, module_box= ? WHERE id=?");
              query.setParameter(1,
                  moduleBoxDto.getTitle() != null ? moduleBoxDto.getTitle().getLanguage(locale)
                      : null);
              query.setParameter(2, enabled);
              query.setParameter(3, superProductId);
              query.executeUpdate();

              // Genera los diferetes modulos BOX (tab1, tab2, ...) para cada uno de los productos
              generateModuleBoxFromConfig(moduleBoxDto, moduleBoxList, locale, superProductId);
            }

          }

          System.out.println("Creando relaciones entre el módulo y los diferentes idiomas ");

          // Relaciona módulos de idiomas por tab
          relatedLanguageModulesByTab(moduleBoxList, ModuleType.BOX);

        }

      }

      // Confirmar la transacción
      entityManager.getTransaction().commit();

    }

    entityManager.close();

  }

  /**
   * Crea un modulo accordion por cada uno de los tabs asociados a un producto (un modulo por cada
   * tab y cada idioma de producto).
   */
  @SuppressWarnings("rawtypes")
  public void createAccordionModuleAndAssociateToProduct(boolean onlyEnabled) {

    // Obtener el listado de superProductos (id de Strapi) y configuraciones del módulo ACCORDION
    List<Object[]> queryResultList =
        this.getModuleConfigFromHola(ModuleType.ACCORDION, onlyEnabled);

    if (!queryResultList.isEmpty()) {

      entityManager.getTransaction().begin();

      // Iteramos sobre cada una de los superproductos
      for (Iterator iterator = queryResultList.iterator(); iterator.hasNext();) {
        Object[] row = (Object[]) iterator.next();

        String productCode = (String) row[0];
        String config = (String) row[1];
        Boolean enabled = (Boolean) row[2];

        System.out.println(String
            .format("Creando módulo Accordion y asociándolo al super producto: %s", productCode));

        // Se añade en STRAPI la configuración del módulo ACCORDION para el producto determinado
        if (config != null) {

          ModuleAccordionDTO moduleAccordionDto =
              JacksonUtils.toObject(config, ModuleAccordionDTO.class);
          Map<Integer, List<Long>> moduleAccordionList = new HashMap<>();

          // Creamos una entrada en el módulo de ACCORDION para cada uno de los idiomas dados
          for (LocaleStrapi locale : LocaleStrapi.getValues()) {

            Integer superProductId = null;
            try {
              // Obtenemos el superproducto asociado al código de producto y en el idioma dado
              Query query = entityManager.createNativeQuery(
                  "SELECT id from CMSSTRAPI.super_products where code = ? and locale = ?");
              query.setParameter(1, productCode);
              query.setParameter(2, locale.getLabel());
              superProductId = (Integer) query.getSingleResult();
            } catch (NoResultException e) {
              System.out.println(String
                  .format("No se encontró en Strapi el producto con código: %s", productCode));
            }


            if (superProductId != null) {
              // Se actualiza el superproducto con el título del modulo box
              Query query = entityManager.createNativeQuery(
                  "UPDATE CMSSTRAPI.super_products SET module_accordion_title= ?, module_accordion= ? WHERE id=?");
              query.setParameter(1,
                  moduleAccordionDto.getTitle() != null
                      ? moduleAccordionDto.getTitle().getLanguage(locale)
                      : null);
              query.setParameter(2, enabled);
              query.setParameter(3, superProductId);
              query.executeUpdate();

              // Genera los diferetes modulos ACCORDION (tab1, tab2, ...) para cada uno de los
              // productos
              generateModuleAccordionFromConfig(moduleAccordionDto, moduleAccordionList, locale,
                  superProductId);
            }

          }

          // Relaciona módulos de idiomas por tab
          relatedLanguageModulesByTab(moduleAccordionList, ModuleType.ACCORDION);

        }

      }

      // Confirmar la transacción
      entityManager.getTransaction().commit();

    }

    entityManager.close();

  }

  /**
   * Activa o desactiva un modulo slider en un super producto (lo hace por cada idioma de producto).
   */
  @SuppressWarnings("rawtypes")
  public void activateOrDesactivateSliderModuleOnProduct(boolean onlyEnabled) {

    // Obtener el listado de superProductos (id de Strapi) y configuraciones del módulo BOX
    List<Object[]> queryResultList = this.getModuleConfigFromHola(ModuleType.SLIDER, onlyEnabled);

    if (!queryResultList.isEmpty()) {

      entityManager.getTransaction().begin();

      // Iteramos sobre cada una de los superproductos
      for (Iterator iterator = queryResultList.iterator(); iterator.hasNext();) {
        Object[] row = (Object[]) iterator.next();

        String productCode = (String) row[0];
        String config = (String) row[1];
        Boolean enabled = (Boolean) row[2];

        // Se añade en STRAPI la configuración del módulo ACCORDION para el producto determinado
        if (config != null) {

          ModuleSliderDTO moduleSliderDto = JacksonUtils.toObject(config, ModuleSliderDTO.class);

          System.out.println(String
              .format("Activando/desactivando módulo Slider del super producto: %s", productCode));

          // Se actualiza el estado del slider por cada uno de los idiomas del producto
          for (LocaleStrapi locale : LocaleStrapi.getValues()) {

            Integer superProductId = null;
            try {
              // Obtenemos el superproducto asociado al código de producto y en el idioma dado
              Query query = entityManager.createNativeQuery(
                  "SELECT id from CMSSTRAPI.super_products where code = ? and locale = ?");
              query.setParameter(1, productCode);
              query.setParameter(2, locale.getLabel());
              superProductId = (Integer) query.getSingleResult();
            } catch (NoResultException e) {
              System.out.println(
                  String.format("No se encontró en Strapi el producto con código: %s e idioma: %s",
                      productCode, locale));
            }

            if (superProductId != null) {
              // Se actualiza el superproducto con el estado del modulo slider
              Query query = entityManager.createNativeQuery(
                  "UPDATE CMSSTRAPI.super_products SET module_slider_title= ?, module_slider= ? WHERE id=?");
              query.setParameter(1,
                  moduleSliderDto.getTitle() != null
                      ? moduleSliderDto.getTitle().getLanguage(locale)
                      : null);
              query.setParameter(2, enabled);
              query.setParameter(3, superProductId);
              query.executeUpdate();
            }

          }
        }

      }

      // Confirmar la transacción
      entityManager.getTransaction().commit();
    }

    entityManager.close();

  }

  /**
   * Activa o desactiva un modulo corporación logo en un super producto (lo hace por cada idioma de
   * producto).
   */
  @SuppressWarnings("rawtypes")
  public void activateOrDesactivateLogoModuleOnProduct(boolean onlyEnabled) {

    // Obtener el listado de superProductos (id de Strapi) y configuraciones del módulo BOX
    List<Object[]> queryResultList =
        this.getModuleConfigFromHola(ModuleType.CORPORATION_LOGOS, onlyEnabled);

    if (!queryResultList.isEmpty()) {

      entityManager.getTransaction().begin();

      // Iteramos sobre cada una de los superproductos
      for (Iterator iterator = queryResultList.iterator(); iterator.hasNext();) {
        Object[] row = (Object[]) iterator.next();

        String productCode = (String) row[0];
        Boolean enabled = (Boolean) row[2];

        System.out.println(String.format(
            "Activando/desactivando módulo Corporación logo del super producto: %s", productCode));

        // Se actualiza el estado del slider por cada uno de los idiomas del producto
        for (LocaleStrapi locale : LocaleStrapi.getValues()) {

          Integer superProductId = null;
          try {
            // Obtenemos el superproducto asociado al código de producto y en el idioma dado
            Query query = entityManager.createNativeQuery(
                "SELECT id from CMSSTRAPI.super_products where code = ? and locale = ?");
            query.setParameter(1, productCode);
            query.setParameter(2, locale.getLabel());
            superProductId = (Integer) query.getSingleResult();
          } catch (NoResultException e) {
            System.out.println(
                String.format("No se encontró en Strapi el producto con código: %s", productCode));
          }

          if (superProductId != null) {

            // Se actualiza el superproducto con el estado del modulo corporación logo
            Query query = entityManager.createNativeQuery(
                "UPDATE CMSSTRAPI.super_products SET  module_corporation_logo= ? WHERE id=?");
            query.setParameter(1, enabled);
            query.setParameter(2, superProductId);
            query.executeUpdate();
          }

        }

      }

      // Confirmar la transacción
      entityManager.getTransaction().commit();

    }

    entityManager.close();

  }

  /**
   * Inicializa los modulos de todos los superproductos.
   */
  public void initializeModules() {
    entityManager = EntityManagerUtil.getMySQLEntityManager();
    entityManager.getTransaction().begin();

    Query query = entityManager.createNativeQuery(
        "UPDATE CMSSTRAPI.super_products SET module_slider = 0, module_box = 0, module_accordion = 0, module_link_download= 0, "
            + "module_corporation_logo = 0, module_box_title = null, module_accordion_title = null, module_slider_title = null");
    query.executeUpdate();
    entityManager.getTransaction().commit();


    entityManager.close();

  }

  /**
   * A partir de la configuración del módulo procedente de HOLA se creará un modulo BOX. Uno por
   * cada tab y por cada idioma.
   * 
   * @param moduleBoxDto
   * @param moduleBoxList
   * @param locale
   * @param superProductId
   */
  private void generateModuleBoxFromConfig(ModuleBoxDTO moduleBoxDto,
      Map<Integer, List<Long>> moduleBoxList, LocaleStrapi locale, Integer superProductId) {

    Date today = new Date();

    // Se creará un modulo por cada tab (4 tab)
    for (int i = 0; i < 4; i++) {

      if (moduleBoxDto.getTab(i + 1) != null) {

        // Se crea una entrada en el modulo BOX para el producto dado
        Query query = entityManager.createNativeQuery(
            "INSERT INTO CMSSTRAPI.module_boxes (title, tag, link, enabled, created_at, updated_at, locale) values (?, ?, ?, ?, ?, ?, ?)");
        query.setParameter(1, moduleBoxDto.getTab(i + 1).getTitle().getLanguage(locale));
        query.setParameter(2, moduleBoxDto.getTab(i + 1).getTag().getLanguage(locale));
        query.setParameter(3, moduleBoxDto.getTab(i + 1).getLink().getLanguage(locale));
        query.setParameter(4,
            Integer.parseInt(moduleBoxDto.getTab(i + 1).getEnabled().getLanguage(locale)));
        query.setParameter(5, today);
        query.setParameter(6, today);
        query.setParameter(7, locale.getLabel());
        query.executeUpdate();

        String imageOrders = moduleBoxDto.getTab(i + 1).getItemsOrder();

        // Obtén el ID generado por la base de datos
        Query idQuery = entityManager.createNativeQuery("SELECT LAST_INSERT_ID()");
        Long moduleBoxId = ((Number) idQuery.getSingleResult()).longValue();

        // Agrupamos el listado de identificadores de modulo para los diferentes idiomas en función
        // del tab
        moduleBoxList.computeIfAbsent(i + 1, k -> new ArrayList<>()).add(moduleBoxId);

        createProductModuleRelationAndAssignImages(imageOrders, superProductId, moduleBoxId,
            ModuleType.BOX);
      }
    }

  }

  /**
   * A partir de la configuración del módulo procedente de HOLA se creará un modulo LINK DOWNLOAD.
   * Uno por cada idioma.
   * 
   * @param moduleLinkDto
   * @param moduleBoxList
   * @param locale
   * @param superProductId
   */
  private void generateModuleLinkDownloadFromConfig(ModuleLinkDownloadDTO moduleLinkDto,
      List<Long> moduleLinkList, LocaleStrapi locale, Integer superProductId) {

    Date today = new Date();

    // Se crea una entrada en el modulo BOX para el producto dado
    Query query = entityManager.createNativeQuery(
        "INSERT INTO CMSSTRAPI.module_link_downloads (title, link, created_at, updated_at, locale) values (?, ?, ?, ?, ?)");
    query.setParameter(1, moduleLinkDto.getTitle().getLanguage(locale));
    query.setParameter(2, moduleLinkDto.getLink().getLanguage(locale));
    query.setParameter(3, today);
    query.setParameter(4, today);
    query.setParameter(5, locale.getLabel());
    query.executeUpdate();

    // Obtén el ID generado por la base de datos
    Query idQuery = entityManager.createNativeQuery("SELECT LAST_INSERT_ID()");
    Long moduleLinkId = ((Number) idQuery.getSingleResult()).longValue();

    // Agrupamos el listado de identificadores de modulo para los diferentes idiomas
    moduleLinkList.add(moduleLinkId);
    String imageOrders = null;

    createProductModuleRelationAndAssignImages(imageOrders, superProductId, moduleLinkId,
        ModuleType.LINK_DOWNLOAD);

  }

  /**
   * A partir de la configuración del módulo procedente de HOLA se creará un modulo ACCORDION. Uno
   * por cada tab y por cada idioma.
   * 
   * @param moduleAccordionDto
   * @param moduleAccordionList
   * @param locale
   * @param superProductId
   */
  private void generateModuleAccordionFromConfig(ModuleAccordionDTO moduleAccordionDto,
      Map<Integer, List<Long>> moduleAccordionList, LocaleStrapi locale, Integer superProductId) {

    Date today = new Date();

    // Se creará un modulo por cada tab (4 tab)
    for (int i = 0; i < 4; i++) {

      if (moduleAccordionDto.getTab(i + 1) != null) {

        // Se crea una entrada en el modulo ACCORDION para el producto dado
        Query query = entityManager.createNativeQuery(
            "INSERT INTO CMSSTRAPI.module_accordions (title, description, title_link, link, category_analytics, title_class, enabled, created_at, updated_at, locale) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        query.setParameter(1, moduleAccordionDto.getTab(i + 1).getTitle().getLanguage(locale));
        query.setParameter(2,
            moduleAccordionDto.getTab(i + 1).getDescription().getLanguage(locale));
        query.setParameter(3, moduleAccordionDto.getTab(i + 1).getTitleLink().getLanguage(locale));
        query.setParameter(4, moduleAccordionDto.getTab(i + 1).getLink().getLanguage(locale));
        query.setParameter(5,
            moduleAccordionDto.getTab(i + 1).getCategoryAnalytics().getLanguage(locale));
        query.setParameter(6, moduleAccordionDto.getTab(i + 1).getTitleClass());
        query.setParameter(7,
            Integer.parseInt(moduleAccordionDto.getTab(i + 1).getEnabled().getLanguage(locale)));
        query.setParameter(8, today);
        query.setParameter(9, today);
        query.setParameter(10, locale.getLabel());
        query.executeUpdate();

        String imageOrders = moduleAccordionDto.getTab(i + 1).getItemsOrder();

        // Obtén el ID generado por la base de datos
        Query idQuery = entityManager.createNativeQuery("SELECT LAST_INSERT_ID()");
        Long moduleAccordionId = ((Number) idQuery.getSingleResult()).longValue();

        // Agrupamos el listado de identificadores de modulo para los diferentes idiomas en función
        // del tab
        moduleAccordionList.computeIfAbsent(i + 1, k -> new ArrayList<>()).add(moduleAccordionId);

        createProductModuleRelationAndAssignImages(imageOrders, superProductId, moduleAccordionId,
            ModuleType.ACCORDION);
      }
    }

  }


  @SuppressWarnings("unchecked")
  private void createProductModuleRelationAndAssignImages(String imageOrders,
      Integer superProductId, Long moduleId, ModuleType moduleType) {
    Query query;

    String sql = ("INSERT INTO CMSSTRAPI.module_Y_super_product_id_links "
        + "(module_X_id, super_product_id, module_X_order) values (?, ?, 1)")
            .replaceAll("Y", moduleType.getTable()).replaceAll("X", moduleType.getField());

    // Se crea una entrada en el modulo BOX para el producto dado
    query = entityManager.createNativeQuery(sql);
    query.setParameter(1, moduleId);
    query.setParameter(2, superProductId);
    query.executeUpdate();


    if (!StringUtils.isNullOrEmpty(imageOrders)) {

      List<Integer> imageOrderList =
          Arrays.stream(imageOrders.split(",")).map(Integer::parseInt).collect(Collectors.toList());

      // Obtenemos los identificadores de imágenes de Strapi asociados a los de HOLA
      query = entityManager.createNativeQuery(
          "SELECT image_strapi FROM CMSSTRAPI.image_relateds WHERE image_ecnr in (:imageOrders)");
      query.setParameter("imageOrders", imageOrderList);

      List<Integer> queryResultList = query.getResultList();

      if (queryResultList != null) {
        int imageOrder = 1;

        // Iteramos sobre cada una de los identificadores de imágenes
        for (Integer imageId : queryResultList) {

          // Se añade la imagen principal
          query = entityManager.createNativeQuery(
              "INSERT INTO CMSSTRAPI.files_related_morphs (file_id, related_id, related_type, field, `order`) VALUES (?, ?, ?, ?, ?)");
          query.setParameter(1, imageId);
          query.setParameter(2, moduleId);
          query.setParameter(3, ImageRelatedType.MODULE_BOX.getLabel());
          query.setParameter(4, ImageField.IMAGE_MODULE.getLabel());
          query.setParameter(5, imageOrder);
          query.executeUpdate();

          imageOrder++;
        }
      }

    }
  }


  /**
   * Creando relaciones entre los modulos en diferentes idiomas para cada Tab de producto.
   * 
   * @param moduleBoxList listado de identificadores de módulos en relación al tab de producto.
   */
  private void relatedLanguageModulesByTab(Map<Integer, List<Long>> moduleBoxList,
      ModuleType moduleType) {

    System.out.println("Creando relaciones entre el módulo y los diferentes idiomas por cada tab");

    for (Map.Entry<Integer, List<Long>> entry : moduleBoxList.entrySet()) {
      List<Long> moduleBoxIds = entry.getValue();

      this.relatedLanguageModules(moduleBoxIds, moduleType);
    }

  }

  /**
   * Creando relaciones entre los modulos en diferentes idiomas para cada Tab de producto.
   * 
   * @param moduleBoxList listado de identificadores de módulos en relación al tab de producto.
   */
  private void relatedLanguageModules(List<Long> moduleBoxIds, ModuleType moduleType) {

    String sql = ("INSERT INTO CMSSTRAPI.module_Y_localizations_links "
        + "(module_X_id, inv_module_X_id, module_X_order) values (?, ?, ?)")
            .replaceAll("Y", moduleType.getTable()).replaceAll("X", moduleType.getField());

    for (int i = 0; i < moduleBoxIds.size(); i++) {
      for (int j = 0; j < moduleBoxIds.size(); j++) {
        if (i != j) {
          // Se crea una entrada en la tabla que relaciona los distintos modulos entre si a
          // partir de los idiomas definidos.
          Query query = entityManager.createNativeQuery(sql);
          query.setParameter(1, moduleBoxIds.get(i));
          query.setParameter(2, moduleBoxIds.get(j));
          query.setParameter(3, j + 1);
          query.executeUpdate();
        }
      }

    }

  }

  @SuppressWarnings("unchecked")
  private List<Object[]> getModuleConfigFromHola(ModuleType moduleType, boolean onlyEnabled) {

    Query query;
    List<Object[]> queryResultList;
    entityManager = EntityManagerUtil.getMySQLEntityManager();

    if (onlyEnabled) {

      // Obtiene el listado de superProductos (id de Strapi) y configuraciones del módulo
      // determinado que estén habilitados
      query = entityManager.createNativeQuery(
          "SELECT p.superproduct_id, pm.config, pm.enabled FROM product_module pm "
              + "left join product p on pm.product_id = p.id left join module m on pm.module_id = m.id "
              + "WHERE p.superproduct_id != '' and m.name = ? and pm.enabled = ?");
      query.setParameter(1, moduleType.name());
      query.setParameter(2, 1); // Solo carga los marcados como activos

    } else {
      // Obtiene el listado de superProductos (id de Strapi) y configuraciones del módulo
      // determinado
      query = entityManager.createNativeQuery(
          "SELECT p.superproduct_id, pm.config, pm.enabled FROM product_module pm "
              + "left join product p on pm.product_id = p.id left join module m on pm.module_id = m.id "
              + "WHERE p.superproduct_id != '' and m.name = ?");
      query.setParameter(1, moduleType.name());
    }
    queryResultList = query.getResultList();
    return queryResultList;
  }
}
