package strapi.cms.load.dto;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataRequestDTO implements Serializable {

  private static final long serialVersionUID = -1165906533337034978L;

  private PublishRequestDTO data;

  public DataRequestDTO() {}

  public DataRequestDTO(Date data) {
    super();
    this.data = new PublishRequestDTO(data);
  }

  public PublishRequestDTO getData() {
    return data;
  }

  public void setData(PublishRequestDTO data) {
    this.data = data;
  }



}
