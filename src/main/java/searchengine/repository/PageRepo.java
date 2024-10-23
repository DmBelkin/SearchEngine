package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Page;

import java.util.Optional;

@Repository
public interface PageRepo extends JpaRepository<Page, Integer> {

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE search_engine.page", nativeQuery = true)
    void truncate();

    long countBySiteId(long site_id);

    Page findBypath(String path);

    Optional<Page> findById(Long id);

}


