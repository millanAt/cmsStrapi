package strapi.cms.module.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ModuleTabDTO {

  private LanguageDescriptionDTO title;
  private LanguageDescriptionDTO tag;
  private LanguageDescriptionDTO link;
  private LanguageDescriptionDTO enabled;

  @JsonProperty("items_order")
  private String itemsOrder;

  public LanguageDescriptionDTO getTitle() {
    return title;
  }

  public void setTitle(LanguageDescriptionDTO title) {
    this.title = title;
  }

  public LanguageDescriptionDTO getTag() {
    return tag;
  }

  public void setTag(LanguageDescriptionDTO tag) {
    this.tag = tag;
  }

  public LanguageDescriptionDTO getLink() {
    return link;
  }

  public void setLink(LanguageDescriptionDTO link) {
    this.link = link;
  }

  public LanguageDescriptionDTO getEnabled() {
    return enabled;
  }

  public void setEnabled(LanguageDescriptionDTO enabled) {
    this.enabled = enabled;
  }

  public String getItemsOrder() {
    return itemsOrder;
  }

  public void setItemsOrder(String itemsOrder) {
    this.itemsOrder = itemsOrder;
  }



}
