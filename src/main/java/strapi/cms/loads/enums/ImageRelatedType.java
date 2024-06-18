package strapi.cms.loads.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Tipos de relaciones de imágenes de Strapi
 * 
 * @author upe00901
 *
 */
public enum ImageRelatedType {
    SUPER_PRODUCT("api::super-product.super-product"),
    MODULE_BOX("api::module-box.module-box");

  private String label;

  private ImageRelatedType(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static List<ImageRelatedType> getValues() {
    List<ImageRelatedType> labels = new ArrayList<ImageRelatedType>();
    for (ImageRelatedType item : ImageRelatedType.values()) {
      labels.add(item);
    }
    return labels;
  }

}
