package strapi.cms.load.utils;

import java.util.Arrays;
import java.util.List;

public class Constants {

  public static final String IMAGE_URL_PRE =
      "https://pre.static.holabarcelona.com/images/%s/resize/%s/%s/%s.%s";
  public static final String IMAGE_URL_PRO =
      "https://pro.static.holabarcelona.com/images/%s/resize/%s/%s/%s.%s";

  public static final String BASE_URL_PRE = "https://pre.c1.cloud-2.tmb.cat/cms";
  public static final String BASE_URL_PRO = "https://pro.c1.cloud-2.tmb.cat/cms";
  public static final String RESOURCE_DIR = "src/main/resources/";

  public static final String STRAPI_COLLECTIONS =
      "countries,channels,super_products,group_entities,promos,super_product_group_mis,super_product_channels,related_super_products,products,variants,vouchers,module_boxes,module_link_downloads,module_accordions,widget_types,widgets,widget_pages,op_verificadas_configs";

  public static final String STRAPI_GROUP_ENTITIES = "group_entities";
  public static final String STRAPI_SP_GROUP_ENTITY = "super_products_groups";
  public static final List<String> STRAPI_SP_GROUP_LIST = Arrays.asList("ECNR_BARCELONA_TOURS",
      "ECNR_EXCURSIONES", "ECNR_PACKS", "ECNR_PANORAMIC", "ECNR_PUBLIC_TRANSPORT");
}
