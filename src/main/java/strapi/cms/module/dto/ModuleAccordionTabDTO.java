package strapi.cms.module.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ModuleAccordionTabDTO {

  private LanguageDescriptionDTO title;

  private LanguageDescriptionDTO description;

  @JsonProperty("title_link")
  private LanguageDescriptionDTO titleLink;

  private LanguageDescriptionDTO link;

  @JsonProperty("category_analytics")
  private LanguageDescriptionDTO categoryAnalytics;

  @JsonProperty("title_class")
  private String titleClass;

  @JsonProperty("items_order")
  private String itemsOrder;

  private LanguageDescriptionDTO enabled;

  public LanguageDescriptionDTO getTitle() {
    return title;
  }

  public void setTitle(LanguageDescriptionDTO title) {
    this.title = title;
  }

  public LanguageDescriptionDTO getDescription() {
    return description;
  }

  public void setDescription(LanguageDescriptionDTO description) {
    this.description = description;
  }

  public LanguageDescriptionDTO getTitleLink() {
    return titleLink;
  }

  public void setTitleLink(LanguageDescriptionDTO titleLink) {
    this.titleLink = titleLink;
  }

  public LanguageDescriptionDTO getLink() {
    return link;
  }

  public void setLink(LanguageDescriptionDTO link) {
    this.link = link;
  }

  public LanguageDescriptionDTO getCategoryAnalytics() {
    return categoryAnalytics;
  }

  public void setCategoryAnalytics(LanguageDescriptionDTO categoryAnalytics) {
    this.categoryAnalytics = categoryAnalytics;
  }

  public String getTitleClass() {
    return titleClass;
  }

  public void setTitleClass(String titleClass) {
    this.titleClass = titleClass;
  }

  public String getItemsOrder() {
    return itemsOrder;
  }

  public void setItemsOrder(String itemsOrder) {
    this.itemsOrder = itemsOrder;
  }

  public LanguageDescriptionDTO getEnabled() {
    return enabled;
  }

  public void setEnabled(LanguageDescriptionDTO enabled) {
    this.enabled = enabled;
  }



}
