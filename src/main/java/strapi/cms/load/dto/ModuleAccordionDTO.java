package strapi.cms.load.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ModuleAccordionDTO {

  private LanguageDescriptionDTO title;
  private ModuleAccordionTabDTO tab1;
  private ModuleAccordionTabDTO tab2;
  private ModuleAccordionTabDTO tab3;
  private ModuleAccordionTabDTO tab4;

  public LanguageDescriptionDTO getTitle() {
    return title;
  }

  public void setTitle(LanguageDescriptionDTO title) {
    this.title = title;
  }

  public ModuleAccordionTabDTO getTab1() {
    return tab1;
  }

  public void setTab1(ModuleAccordionTabDTO tab1) {
    this.tab1 = tab1;
  }

  public ModuleAccordionTabDTO getTab2() {
    return tab2;
  }

  public void setTab2(ModuleAccordionTabDTO tab2) {
    this.tab2 = tab2;
  }

  public ModuleAccordionTabDTO getTab3() {
    return tab3;
  }

  public void setTab3(ModuleAccordionTabDTO tab3) {
    this.tab3 = tab3;
  }

  public ModuleAccordionTabDTO getTab4() {
    return tab4;
  }

  public void setTab4(ModuleAccordionTabDTO tab4) {
    this.tab4 = tab4;
  }

  public ModuleAccordionTabDTO getTab(int tab) {
    if (tab == 1) {
      return tab1;
    }
    if (tab == 2) {
      return tab2;
    }
    if (tab == 3) {
      return tab3;
    }
    if (tab == 4) {
      return tab4;
    } else {
      return null;
    }
  }



}
