package strapi.cms.module.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ModuleBoxDTO {

  private LanguageDescriptionDTO title;
  private ModuleTabDTO tab1;
  private ModuleTabDTO tab2;
  private ModuleTabDTO tab3;
  private ModuleTabDTO tab4;

  public LanguageDescriptionDTO getTitle() {
    return title;
  }

  public void setTitle(LanguageDescriptionDTO title) {
    this.title = title;
  }

  public ModuleTabDTO getTab1() {
    return tab1;
  }

  public void setTab1(ModuleTabDTO tab1) {
    this.tab1 = tab1;
  }

  public ModuleTabDTO getTab2() {
    return tab2;
  }

  public void setTab2(ModuleTabDTO tab2) {
    this.tab2 = tab2;
  }

  public ModuleTabDTO getTab3() {
    return tab3;
  }

  public void setTab3(ModuleTabDTO tab3) {
    this.tab3 = tab3;
  }

  public ModuleTabDTO getTab4() {
    return tab4;
  }

  public void setTab4(ModuleTabDTO tab4) {
    this.tab4 = tab4;
  }

  public ModuleTabDTO getTab(int tab) {
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
