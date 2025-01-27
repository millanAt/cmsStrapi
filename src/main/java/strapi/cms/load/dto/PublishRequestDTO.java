package strapi.cms.load.dto;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PublishRequestDTO implements Serializable {

  private static final long serialVersionUID = -1165906533337034978L;

  private Date publishedAt;

  public PublishRequestDTO() {}

  public PublishRequestDTO(Date publishedAt) {
    super();
    this.publishedAt = publishedAt;
  }

  public Date getPublishedAt() {
    return publishedAt;
  }

  public void setPublishedAt(Date publishedAt) {
    this.publishedAt = publishedAt;
  }


}
