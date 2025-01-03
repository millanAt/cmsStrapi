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
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import strapi.cms.load.dto.ImageDTO;
import strapi.cms.load.dto.UploadImageResultDTO;
import strapi.cms.load.service.StrapiRequesterService;
import strapi.cms.load.utils.Constants;
import strapi.cms.load.utils.Utils;
import strapi.cms.loads.enums.ImageField;
import strapi.cms.loads.enums.ImageRelatedType;
import strapi.cms.loads.enums.LocaleStrapi;

@Stateless
public class StrapiImagesBO {

  private EntityManagerFactory emf;
  private EntityManager entityManager;


  public StrapiImagesBO(EntityManagerFactory emf) {
    super();
    this.emf = emf;
  }

  /**
   * Inicializa los modulos de todos los superproductos.
   */
  public void initializeImages() {
    entityManager = emf.createEntityManager();
    entityManager.getTransaction().begin();

    Query query = entityManager.createNativeQuery("delete from CMSSTRAPI.files_related_mph");
    query.executeUpdate();
    query = entityManager.createNativeQuery("delete from CMSSTRAPI.files_folder_lnk");
    query.executeUpdate();
    query = entityManager.createNativeQuery("delete from CMSSTRAPI.files");
    query.executeUpdate();
    query = entityManager.createNativeQuery("delete from CMSSTRAPI.image_relateds");
    query.executeUpdate();

    entityManager.getTransaction().commit();


    entityManager.close();

  }


  @SuppressWarnings({"rawtypes", "unchecked"})
  public void importImagesFromHola() throws IOException {

    // Crear un nuevo EntityManager por transacción o por petición
    entityManager = emf.createEntityManager();

    StrapiRequesterService strapiRequest = new StrapiRequesterService();

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
        imageDto.setWidth(((Integer) row[5]));
        imageDto.setHeight(((Integer) row[6]));
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
                "INSERT INTO CMSSTRAPI.image_relateds (document_id, image_ecnr, image_strapi, created_at, updated_at, created_by_id) VALUES (?, ?, ?, ?, ?, (select MIN(id) from CMSSTRAPI.admin_users))");
            query.setParameter(1, Utils.generateDocumentId());
            query.setParameter(2, imageDto.getImageId());
            query.setParameter(3, uploadResult.getId());
            query.setParameter(4, today);
            query.setParameter(5, today);

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
          "SELECT case WHEN superproduct_id LIKE '712' THEN 'easyMontserratMountain' "
              + "WHEN superproduct_id LIKE '713' THEN 'montserratMountainAndGaudi' "
              + "WHEN superproduct_id LIKE '714' THEN 'pyreneesVallDeNuria' "
              + "WHEN superproduct_id LIKE '715' THEN 'daliMuseumFigueresAndGirona' "
              + "WHEN superproduct_id LIKE '716' THEN 'wineAndCava' "
              + "WHEN superproduct_id LIKE '717' THEN 'laRocaVillageShoppingExpressTour' "
              + "WHEN superproduct_id LIKE '720' THEN 'theMontserratTourAllIncluded' "
              + "ELSE superproduct_id END, images_sort FROM product WHERE superproduct_id IN ("
              + "SELECT case WHEN code LIKE 'easyMontserratMountain' THEN '712' "
              + "WHEN code LIKE 'montserratMountainAndGaudi' THEN '713' "
              + "WHEN code LIKE 'pyreneesVallDeNuria' THEN '714' "
              + "WHEN code LIKE 'daliMuseumFigueresAndGirona' THEN '715' "
              + "WHEN code LIKE 'wineAndCava' THEN '716' "
              + "WHEN code LIKE 'laRocaVillageShoppingExpressTour' THEN '717' "
              + "WHEN code LIKE 'theMontserratTourAllIncluded' THEN '720' "
              + "ELSE code END FROM CMSSTRAPI.super_products)");
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

        // Añadimos relaciones de imagenes superproductos para todos los idiomas de éste
        for (int i = 0; i < imageSortSplit.length; i++) {

          // Creamos una entrada en el módulo de Link Download para cada uno de los idiomas dados
          for (LocaleStrapi locale : LocaleStrapi.getValues()) {

            // Para la primera imagen la añadimos tanto como imagen principal, como al header y al
            // carrusel
            if (i == 0) {

              // Se añade la imagen principal
              query = entityManager.createNativeQuery(
                  "INSERT INTO CMSSTRAPI.files_related_mph (file_id, related_id, related_type, field, `order`) VALUES ((SELECT image_strapi from CMSSTRAPI.image_relateds WHERE image_ecnr = ?), (SELECT id from CMSSTRAPI.super_products WHERE code = ? and locale = ?), ?, ?, ?)");
              query.setParameter(1, imageSortSplit[i]);
              query.setParameter(2, superProductCode);
              query.setParameter(3, locale.getLabel());
              query.setParameter(4, ImageRelatedType.SUPER_PRODUCT.getLabel());
              query.setParameter(5, ImageField.MAIN_IMAGE_SP.getLabel());
              query.setParameter(6, i + 1);
              query.executeUpdate();

            } else if (i == 1) {

              // Se añade la imagen de header
              query = entityManager.createNativeQuery(
                  "INSERT INTO CMSSTRAPI.files_related_mph (file_id, related_id, related_type, field, `order`) VALUES ((SELECT image_strapi from CMSSTRAPI.image_relateds WHERE image_ecnr = ?), (SELECT id from CMSSTRAPI.super_products WHERE code = ? and locale = ?), ?, ?, ?)");
              query.setParameter(1, imageSortSplit[i]);
              query.setParameter(2, superProductCode);
              query.setParameter(3, locale.getLabel());
              query.setParameter(4, ImageRelatedType.SUPER_PRODUCT.getLabel());
              query.setParameter(5, ImageField.HEADER_IMAGE_SP.getLabel());
              query.setParameter(6, i + 1);
              query.executeUpdate();

            } else {

              // Se añade el listado de imágenes al carrusel de imágenes del superProducto
              query = entityManager.createNativeQuery(
                  "INSERT INTO CMSSTRAPI.files_related_mph (file_id, related_id, related_type, field, `order`) VALUES ((SELECT image_strapi from CMSSTRAPI.image_relateds WHERE image_ecnr = ?), (SELECT id from CMSSTRAPI.super_products WHERE code = ? and locale = ?), ?, ?, ?)");
              query.setParameter(1, imageSortSplit[i]);
              query.setParameter(2, superProductCode);
              query.setParameter(3, locale.getLabel());
              query.setParameter(4, ImageRelatedType.SUPER_PRODUCT.getLabel());
              query.setParameter(5, ImageField.IMAGE_SP.getLabel());
              query.setParameter(6, i + 1);
              query.executeUpdate();
            }
          }

        }
      }
      // Confirmar la transacción
      entityManager.getTransaction().commit();

    }
    entityManager.close();
  }

  public Path getImageFromUrl(ImageDTO imageDto) {

    String environment = System.getenv("ENVIRONMENT");
    String baseURL =
        environment.equalsIgnoreCase("pro") ? Constants.IMAGE_URL_PRO : Constants.IMAGE_URL_PRE;

    String imageUrl = String.format(baseURL, imageDto.getImageId(), imageDto.getHeight(),
        imageDto.getWidth(), imageDto.getName(), imageDto.getExtension());

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
