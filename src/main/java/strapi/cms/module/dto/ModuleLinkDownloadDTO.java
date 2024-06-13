package strapi.cms.module.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ModuleLinkDownloadDTO {

  private LanguageDescriptionDTO title;
  private LanguageDescriptionDTO link;

  public LanguageDescriptionDTO getTitle() {
    return title;
  }

  public void setTitle(LanguageDescriptionDTO title) {
    this.title = title;
  }

  public LanguageDescriptionDTO getLink() {
    return link;
  }

  public void setLink(LanguageDescriptionDTO link) {
    this.link = link;
  }

}
