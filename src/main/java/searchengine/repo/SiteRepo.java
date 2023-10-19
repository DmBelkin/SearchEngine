package searchengine.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Site;


@Repository
public interface SiteRepo extends CrudRepository<Site, String> {

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE search_engine.site", nativeQuery = true)
    void truncate();
}


