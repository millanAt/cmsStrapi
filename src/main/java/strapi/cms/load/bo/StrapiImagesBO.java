package strapi.cms.load.bo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import strapi.cms.load.dto.ImageDTO;
import strapi.cms.load.dto.UploadImageResultDTO;
import strapi.cms.load.service.StrapiRequesterService;
import strapi.cms.load.utils.Constants;
import strapi.cms.load.utils.EntityManagerUtil;
import strapi.cms.loads.enums.ImageField;
import strapi.cms.loads.enums.ImageRelatedType;

@Stateless
public class StrapiImagesBO {

  @SuppressWarnings({"rawtypes", "unchecked"})
  public void importImagesFromHola() throws IOException {

    StrapiRequesterService strapiRequest = new StrapiRequesterService();
    EntityManager entityManager = EntityManagerUtil.getMySQLEntityManager();

    // Obtener el listado de imagenes de HOLA
    Query query = entityManager.createNativeQuery("SELECT * FROM image i");
    List<Object[]> queryResultList = query.getResultList();

    if (!queryResultList.isEmpty()) {
      System.out.println(String.format(
          "Descargando %s imagenes de HOLA y guardando relación de identificadores en Strapi",
          queryResultList.size()));

      List<ImageDTO> imageList = new ArrayList<>();
      Date today = new Date();

      entityManager.getTransaction().begin();

      // Iteramos sobre cada una de las imágenes
      for (Iterator iterator = queryResultList.iterator(); iterator.hasNext();) {

        Object[] row = (Object[]) iterator.next();

        ImageDTO imageDto = new ImageDTO();
        imageDto.setImageId(((Number) row[0]).longValue());
        imageDto.setName(((String) row[1]));
        imageDto.setExtension(((String) row[3]));
        imageDto.setWidth(((Integer) row[4]));
        imageDto.setHeight(((Integer) row[5]));
        imageList.add(imageDto);

        // Descargamos la imagen de HOLA en nuestro equipo
        Path destinationFile = this.getImageFromUrl(imageDto);

        // Si existe el fichero
        if (destinationFile != null) {

          // Subimos la imagen a STRAPI
          UploadImageResultDTO uploadResult = strapiRequest.uploadImage(destinationFile.toFile());

          // Se añade en STRAPI la relación id de imagen de ECNR y el id de imagen de STRAPI
          if (uploadResult != null) {
            query = entityManager.createNativeQuery(
                "INSERT INTO CMSSTRAPI.image_relateds (image_ecnr, image_strapi, created_at, updated_at, created_by_id) VALUES (?, ?, ?, ?, ?)");
            query.setParameter(1, imageDto.getImageId());
            query.setParameter(2, uploadResult.getId());
            query.setParameter(3, today);
            query.setParameter(4, today);
            query.setParameter(5, 1);

            // Ejecutar la consulta
            query.executeUpdate();

          }

          Files.deleteIfExists(destinationFile);
        }
      }

      // Confirmar la transacción
      entityManager.getTransaction().commit();

      // Obtener el listado de imagenes asociados a cada superproducto del HOLA
      query = entityManager.createNativeQuery(
          "SELECT superproduct_id, images_sort FROM product WHERE superproduct_id IN (SELECT code FROM CMSSTRAPI.super_products)");
      queryResultList = query.getResultList();

      entityManager.getTransaction().begin();

      // Iteramos sobre cada una de las imágenes
      for (Iterator iterator = queryResultList.iterator(); iterator.hasNext();) {

        Object[] row = (Object[]) iterator.next();
        String superProductCode = ((String) row[0]);
        String imageSortList = ((String) row[1]);

        String[] imageSortSplit = imageSortList.split(",");

        System.out.println(String.format(
            "Añadiendo relación entre imágenes de Hola y el super producto: %s", superProductCode));

        for (int i = 0; i < imageSortSplit.length; i++) {

          // Para la primera imagen la añadimos tanto como imagen principal, como al header y al
          // carrusel
          if (i == 0) {

            // Se añade la imagen principal
            query = entityManager.createNativeQuery(
                "INSERT INTO CMSSTRAPI.files_related_morphs (file_id, related_id, related_type, field, `order`) VALUES ((SELECT image_strapi from CMSSTRAPI.image_relateds WHERE image_ecnr = ?), (SELECT id from CMSSTRAPI.super_products WHERE code = ? and locale = 'en'), ?, ?, ?)");
            query.setParameter(1, imageSortSplit[i]);
            query.setParameter(2, superProductCode);
            query.setParameter(3, ImageRelatedType.SUPER_PRODUCT.getLabel());
            query.setParameter(4, ImageField.MAIN_IMAGE_SP.getLabel());
            query.setParameter(5, i + 1);
            query.executeUpdate();

            // Se añade la imagen de header
            query = entityManager.createNativeQuery(
                "INSERT INTO CMSSTRAPI.files_related_morphs (file_id, related_id, related_type, field, `order`) VALUES ((SELECT image_strapi from CMSSTRAPI.image_relateds WHERE image_ecnr = ?), (SELECT id from CMSSTRAPI.super_products WHERE code = ? and locale = 'en'), ?, ?, ?)");
            query.setParameter(1, imageSortSplit[i]);
            query.setParameter(2, superProductCode);
            query.setParameter(3, ImageRelatedType.SUPER_PRODUCT.getLabel());
            query.setParameter(4, ImageField.HEADER_IMAGE_SP.getLabel());
            query.setParameter(5, i + 1);
            query.executeUpdate();
          }

          // Se añade el listado de imágenes al carrusel de imágenes del superProducto
          query = entityManager.createNativeQuery(
              "INSERT INTO CMSSTRAPI.files_related_morphs (file_id, related_id, related_type, field, `order`) VALUES ((SELECT image_strapi from CMSSTRAPI.image_relateds WHERE image_ecnr = ?), (SELECT id from CMSSTRAPI.super_products WHERE code = ? and locale = 'en'), ?, ?, ?)");
          query.setParameter(1, imageSortSplit[i]);
          query.setParameter(2, superProductCode);
          query.setParameter(3, ImageRelatedType.SUPER_PRODUCT.getLabel());
          query.setParameter(4, ImageField.IMAGE_SP.getLabel());
          query.setParameter(5, i + 1);
          query.executeUpdate();

        }
      }
      // Confirmar la transacción
      entityManager.getTransaction().commit();

    }
    entityManager.close();
  }

  public Path getImageFromUrl(ImageDTO imageDto) {
    String imageUrl = String.format(Constants.IMAGE_URL, imageDto.getImageId(),
        imageDto.getHeight(), imageDto.getWidth(), imageDto.getName(), imageDto.getExtension());
    String resourceDir = Constants.RESOURCE_DIR;
    String destinationFileName = String.format("%s_%s.%s", imageDto.getName(),
        imageDto.getImageId(), imageDto.getExtension());

    Path destinationFile = Paths.get(resourceDir, destinationFileName);
    try {
      // Crear la URL a partir de la cadena
      URL url = new URL(imageUrl);

      // Abrir un InputStream desde la URL
      InputStream in = url.openStream();

      // Escribir el InputStream en el archivo de destino
      Files.copy(in, destinationFile);

      // Cerrar el InputStream
      in.close();

    } catch (Exception e) {
      System.out.println(String.format("No se puede obtener la imagen de la url: %s", imageUrl));
      return null;
    }

    return destinationFile;
  }
}
