package strapi.cms.images.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Campo de imágenes de Strapi
 * 
 * @author upe00901
 *
 */
public enum ImageField {
    IMAGE_MODULE("Images"),
    IMAGE_SP("images"),
    HEADER_IMAGE_SP("headerImage"),
    MAIN_IMAGE_SP("mainImage");

  private String label;

  private ImageField(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static List<ImageField> getValues() {
    List<ImageField> labels = new ArrayList<ImageField>();
    for (ImageField item : ImageField.values()) {
      labels.add(item);
    }
    return labels;
  }

}
