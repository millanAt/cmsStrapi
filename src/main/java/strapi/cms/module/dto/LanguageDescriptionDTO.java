package strapi.cms.module.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import strapi.cms.images.enums.LocaleStrapi;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LanguageDescriptionDTO {

  private String ca;
  private String de;
  private String en;
  private String es;
  private String fr;
  private String it;

  public String getCa() {
    return ca;
  }

  public void setCa(String ca) {
    this.ca = ca;
  }

  public String getDe() {
    return de;
  }

  public void setDe(String de) {
    this.de = de;
  }

  public String getEn() {
    return en;
  }

  public void setEn(String en) {
    this.en = en;
  }

  public String getEs() {
    return es;
  }

  public void setEs(String es) {
    this.es = es;
  }

  public String getFr() {
    return fr;
  }

  public void setFr(String fr) {
    this.fr = fr;
  }

  public String getIt() {
    return it;
  }

  public void setIt(String it) {
    this.it = it;
  }

  public String getLanguage(LocaleStrapi locale) {
    if (locale.isEnglish()) {
      return this.en;
    } else if (locale.isSpanish()) {
      return this.es;
    } else if (locale.isCatalan()) {
      return this.ca;
    } else if (locale.isFrench()) {
      return this.fr;
    } else if (locale.isGerman()) {
      return this.de;
    } else if (locale.isItalian()) {
      return this.it;
    }
    return en;

  }


}
