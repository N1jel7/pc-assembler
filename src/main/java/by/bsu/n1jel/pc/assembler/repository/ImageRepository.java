package by.bsu.n1jel.pc.assembler.repository;

import by.bsu.n1jel.pc.assembler.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Modifying
    @Query(value = "DELETE FROM components_images WHERE image_id = :imageId", nativeQuery = true)
    void unlinkFromComponents(Long imageId);

    @Modifying
    @Query(value = "DELETE FROM builds_images WHERE image_id = :imageId", nativeQuery = true)
    void unlinkFromBuilds(Long imageId);

    @Modifying
    @Query(value = "UPDATE producers SET image_id = NULL WHERE image_id = :imageId", nativeQuery = true)
    void unlinkFromProducers(Long imageId);
}
