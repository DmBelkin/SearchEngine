package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Indexes;

import java.util.List;


@Repository
public interface IndexRepo extends JpaRepository<Indexes, Integer> {

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE search_engine.indexes", nativeQuery = true)
    void truncate();

    @Query(value = "SELECT indexes FROM Indexes indexes WHERE indexes.lemmaId = :lemma_Id")
    List<Indexes> findAllByLemmaId(@Param("lemma_Id") long lemmaId);
}
