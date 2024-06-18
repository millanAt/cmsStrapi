package strapi.cms.load.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ModuleSliderDTO {

  private LanguageDescriptionDTO title;

  public LanguageDescriptionDTO getTitle() {
    return title;
  }

  public void setTitle(LanguageDescriptionDTO title) {
    this.title = title;
  }

}
