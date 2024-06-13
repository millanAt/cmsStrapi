package strapi.cms.images.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Campo de imágenes de Strapi
 * 
 * @author upe00901
 *
 */
public enum LocaleStrapi {
    ENGLISH("en"),
    CATALAN("ca"),
    SPANISH("es"),
    FRENCH("fr"),
    GERMAN("de"),
    ITALIAN("it");

  private String label;

  private LocaleStrapi(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static List<LocaleStrapi> getValues() {
    List<LocaleStrapi> labels = new ArrayList<LocaleStrapi>();
    for (LocaleStrapi item : LocaleStrapi.values()) {
      labels.add(item);
    }
    return labels;
  }

  public boolean isEnglish() {
    return this.equals(ENGLISH);
  }

  public boolean isCatalan() {
    return this.equals(CATALAN);
  }

  public boolean isSpanish() {
    return this.equals(SPANISH);
  }

  public boolean isFrench() {
    return this.equals(FRENCH);
  }

  public boolean isGerman() {
    return this.equals(GERMAN);
  }

  public boolean isItalian() {
    return this.equals(ITALIAN);
  }

}
