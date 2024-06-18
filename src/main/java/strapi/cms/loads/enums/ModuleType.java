package strapi.cms.loads.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Campo de imágenes de Strapi
 * 
 * @author upe00901
 *
 */
public enum ModuleType {
    BOX("boxes", "box"),
    ACCORDION("accordions", "accordion"),
    LINK_DOWNLOAD("link_downloads", "link_download"),
    SLIDER("sliders", "slider"),
    CORPORATION_LOGOS("corporation_logos", "corporation_logo");


  private String table;
  private String field;

  private ModuleType(String table, String field) {
    this.table = table;
    this.field = field;
  }

  public String getTable() {
    return table;
  }

  public String getField() {
    return field;
  }

  public static List<ModuleType> getValues() {
    List<ModuleType> labels = new ArrayList<ModuleType>();
    for (ModuleType item : ModuleType.values()) {
      labels.add(item);
    }
    return labels;
  }

  public boolean isBox() {
    return this.equals(BOX);
  }

  public boolean isAccordion() {
    return this.equals(ACCORDION);
  }

  public boolean isLinkDownload() {
    return this.equals(LINK_DOWNLOAD);
  }
}
