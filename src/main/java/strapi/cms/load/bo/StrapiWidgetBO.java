package strapi.cms.load.bo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import strapi.cms.load.utils.EntityManagerUtil;

@Stateless
public class StrapiWidgetBO {

  private EntityManager entityManager;

  /**
   * Inicializa los modulos de todos los superproductos.
   */
  public void initializeWidgets() {
    entityManager = EntityManagerUtil.getMySQLEntityManager();
    entityManager.getTransaction().begin();

    Query query =
        entityManager.createNativeQuery("delete from CMSSTRAPI.widgets_widget_type_links");
    query.executeUpdate();
    query = entityManager.createNativeQuery("delete from CMSSTRAPI.widget_types");
    query.executeUpdate();
    query = entityManager.createNativeQuery("delete from CMSSTRAPI.widget_pages_widgets_links");
    query.executeUpdate();
    query =
        entityManager.createNativeQuery("delete from CMSSTRAPI.widget_pages_localizations_links");
    query.executeUpdate();
    query = entityManager.createNativeQuery("delete from CMSSTRAPI.widget_pages");
    query.executeUpdate();
    query = entityManager.createNativeQuery("delete from CMSSTRAPI.widgets");
    query.executeUpdate();
    entityManager.getTransaction().commit();


    entityManager.close();

  }

  /**
   * Importa los tipos de widgets procedentes de HOLA
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void createWidgetType() {

    System.out.println("----------  Creando widget types en Strapi ----------");

    entityManager = EntityManagerUtil.getMySQLEntityManager();

    // Obtiene el listado de tipos de widgets
    Query query = entityManager
        .createNativeQuery("SELECT name, configuration, config_required FROM widget_type");

    List<Object[]> queryResultList = query.getResultList();
    Date today = new Date();

    if (!queryResultList.isEmpty()) {

      entityManager.getTransaction().begin();

      // Iteramos sobre cada uno de los tipos de widgets
      for (Iterator iterator = queryResultList.iterator(); iterator.hasNext();) {
        Object[] row = (Object[]) iterator.next();

        String name = (String) row[0];
        String config = (String) row[1];
        Boolean enabled = (Boolean) row[2];

        System.out.println(String.format("Importando widget de tipo: %s", name));

        query = entityManager.createNativeQuery(
            "INSERT CMSSTRAPI.widget_types (name, configuration, config_required, created_at, updated_at, published_at, created_by_id, updated_by_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        query.setParameter(1, name);
        query.setParameter(2, StringUtils.isNotBlank(config) ? config : null);
        query.setParameter(3, enabled);
        query.setParameter(4, today);
        query.setParameter(5, today);
        query.setParameter(6, today);
        query.setParameter(7, 1);
        query.setParameter(8, 1);
        query.executeUpdate();
      }

      // Confirmar la transacción
      entityManager.getTransaction().commit();

    }

    entityManager.close();

    System.out.println("Se han creado todos los widget types en Strapi");
  }

  /**
   * Importa los widgets procedentes de HOLA
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void createWidgets() {

    System.out.println("----------  Creando widget en Strapi ----------   ");

    entityManager = EntityManagerUtil.getMySQLEntityManager();

    // Obtiene el listado de widgets
    Query query = entityManager.createNativeQuery(
        "SELECT w.name, wt.name as typeName, w.configuration, w.active from widget w "
            + "LEFT JOIN widget_type wt ON w.type = wt.id");

    List<Object[]> queryResultList = query.getResultList();
    Date today = new Date();

    if (!queryResultList.isEmpty()) {

      entityManager.getTransaction().begin();

      // Iteramos sobre cada uno de los widgets
      for (Iterator iterator = queryResultList.iterator(); iterator.hasNext();) {
        Object[] row = (Object[]) iterator.next();

        String name = (String) row[0];
        String typeName = (String) row[1];
        String config = (String) row[2];
        Boolean active = (Boolean) row[3];

        System.out.println(String.format("Importando widget: %s", name));

        query = entityManager.createNativeQuery("INSERT INTO CMSSTRAPI.widgets "
            + "(name, configuration, active, created_at, updated_at, published_at, created_by_id, updated_by_id) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        query.setParameter(1, name);
        query.setParameter(2, StringUtils.isNotBlank(config) ? config : null);
        query.setParameter(3, active);
        query.setParameter(4, today);
        query.setParameter(5, today);
        query.setParameter(6, today);
        query.setParameter(7, 1);
        query.setParameter(8, 1);
        query.executeUpdate();

        // Obtén el ID generado por la base de datos
        Query idQuery = entityManager.createNativeQuery("SELECT LAST_INSERT_ID()");
        Long widgetId = ((Number) idQuery.getSingleResult()).longValue();

        query = entityManager.createNativeQuery("INSERT INTO CMSSTRAPI.widgets_widget_type_links "
            + "(widget_id, widget_type_id) VALUES (?, (SELECT id FROM CMSSTRAPI.widget_types WHERE name = ?))");
        query.setParameter(1, widgetId);
        query.setParameter(2, typeName);
        query.executeUpdate();
      }

      // Confirmar la transacción
      entityManager.getTransaction().commit();

    }

    entityManager.close();

    System.out.println("Se han creado todos los widget en Strapi");
  }

  /**
   * Importa los widgets pages procedentes de HOLA
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void createWidgetPages() {

    System.out.println(" ----------  Creando widget pages en Strapi ---------------");

    entityManager = EntityManagerUtil.getMySQLEntityManager();
    Map<String, List<Long>> widgetPageIdMap = new HashMap<>();

    // Obtiene el listado de widgets
    Query query = entityManager.createNativeQuery("SELECT wp.name, wp.path, wp.enabled, "
        + "COALESCE(MAX(CASE WHEN et.entity_field = 'meta_title' THEN et.translation END), '') AS metaTitle, "
        + "COALESCE(MAX(CASE WHEN et.entity_field = 'meta_keywords' THEN et.translation END), '') AS metaKeywords, "
        + "COALESCE(MAX(CASE WHEN et.entity_field = 'meta_description' THEN et.translation END), '') AS metaDesc, "
        + "locales.locale as locale FROM widget_page wp " + "CROSS JOIN ("
        + "     SELECT DISTINCT locale FROM entity_translation "
        + "     WHERE entity_type = 'widget_page') locales "
        + "LEFT JOIN entity_translation et ON et.entity_id = wp.id AND et.entity_type = 'widget_page' "
        + "AND et.locale = locales.locale "
        + "GROUP BY wp.name, wp.path, wp.enabled, locales.locale ORDER BY "
        + "    CASE WHEN locales.locale = 'en' THEN 0 ELSE 1 END, locales.locale, wp.id");

    List<Object[]> widgetPageList = query.getResultList();
    Date today = new Date();

    if (!widgetPageList.isEmpty()) {

      entityManager.getTransaction().begin();

      // Iteramos sobre cada uno de los widgets
      for (Iterator wpIt = widgetPageList.iterator(); wpIt.hasNext();) {
        Object[] row = (Object[]) wpIt.next();

        String name = (String) row[0];
        String path = (String) row[1];
        Boolean enabled = (Boolean) row[2];
        String metaTitle = (String) row[3];
        String metaKeywords = (String) row[4];
        String metaDesc = (String) row[5];
        String locale = (String) row[6];

        if (locale.equals("en")) {
          System.out
              .println(String.format("Importando widget page: %s en los diferentes idiomas", name));
        }

        query = entityManager.createNativeQuery("INSERT INTO CMSSTRAPI.widget_pages "
            + "(name, path, enabled, meta_title, meta_keywords, meta_description, locale, created_at, updated_at, published_at, created_by_id, updated_by_id) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        query.setParameter(1, name);
        query.setParameter(2, path);
        query.setParameter(3, enabled);
        query.setParameter(4, metaTitle);
        query.setParameter(5, metaKeywords);
        query.setParameter(6, metaDesc);
        query.setParameter(7, locale);
        query.setParameter(8, today);
        query.setParameter(9, today);
        query.setParameter(10, today);
        query.setParameter(11, 1);
        query.setParameter(12, 1);
        query.executeUpdate();

        // Obtén el ID generado por la base de datos
        Query idQuery = entityManager.createNativeQuery("SELECT LAST_INSERT_ID()");
        Long widgetPageId = ((Number) idQuery.getSingleResult()).longValue();
        widgetPageIdMap.computeIfAbsent(name, k -> new ArrayList<>()).add(widgetPageId);

      }

      // Se añaden las relaciones idiomáticas entre los diferentes widgets
      widgetPageIdMap.forEach((name, ids) -> {
        this.relatedLanguageWidgets(name, ids);

        // Creamos links entre widgets y widgetPages
        for (Long id : ids) {
          this.createLinkBetweenWidgetAndWidgetPage(name, id);
        }

      });

      // Confirmar la transacción
      entityManager.getTransaction().commit();

    }

    entityManager.close();

    System.out.println("Se han creado todos los widget pages en Strapi");
  }

  /**
   * Crea relaciones entre widgets page y widgets
   * 
   * @param name
   * @param widgetPageId
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  private void createLinkBetweenWidgetAndWidgetPage(String name, Long widgetPageId) {

    System.out.println("Generando relaciones entre widgets para el widget page: " + name);

    Query query;
    Object[] row;
    // Obtiene el listado de widgets
    query = entityManager
        .createNativeQuery("SELECT w.name, ROW_NUMBER() OVER (ORDER BY position) AS order_page "
            + "FROM widget_location wl LEFT JOIN widget w ON w.id = wl.widget_id "
            + "WHERE widgetPage_id = (SELECT id FROM widget_page WHERE name = ?)");
    query.setParameter(1, name);
    List<Object[]> widgetLocationList = query.getResultList();

    if (!widgetLocationList.isEmpty()) {

      // Iteramos sobre cada una de los superproductos
      for (Iterator locationIt = widgetLocationList.iterator(); locationIt.hasNext();) {
        row = (Object[]) locationIt.next();

        String widgetName = (String) row[0];
        Long orderPage = ((Number) row[1]).longValue();

        query = entityManager.createNativeQuery("INSERT INTO CMSSTRAPI.widget_pages_widgets_links "
            + "(widget_page_id, widget_id, widget_order) VALUES (?, (SELECT id FROM CMSSTRAPI.widgets WHERE name = ?), ?)");
        query.setParameter(1, widgetPageId);
        query.setParameter(2, widgetName);
        query.setParameter(3, orderPage);
        query.executeUpdate();
      }
    }
  }

  /**
   * Creando relaciones entre los modulos en diferentes idiomas para cada widget page.
   * 
   * @param name widget page name.
   * @param moduleBoxList listado de identificadores de widgets pages en diferentes idiomas
   *        asociados a uno determinado.
   */
  private void relatedLanguageWidgets(String name, List<Long> widgetIds) {

    System.out.println("Generando relaciones idiomaticas para el widget page: " + name);

    String sql = ("INSERT INTO CMSSTRAPI.widget_pages_localizations_links "
        + "(widget_page_id, inv_widget_page_id, widget_page_order) values (?, ?, ?)");

    for (int i = 0; i < widgetIds.size(); i++) {
      for (int j = 0; j < widgetIds.size(); j++) {
        if (i != j) {
          // Se crea una entrada en la tabla que relaciona los distintos modulos entre si a
          // partir de los idiomas definidos.
          Query query = entityManager.createNativeQuery(sql);
          query.setParameter(1, widgetIds.get(i));
          query.setParameter(2, widgetIds.get(j));
          query.setParameter(3, j + 1);
          query.executeUpdate();
        }
      }

    }

  }

}

