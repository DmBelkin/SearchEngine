package searchengine.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Page;

@Repository
public interface PageRepo extends CrudRepository<Page, Integer> {

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE search_engine.page", nativeQuery = true)
    void truncate();

}


