package searchengine.repository;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.repository.configuration.RedisRepositoriesRegistrar;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Lemma;

import java.util.List;

@Repository
public interface LemmaRepo extends JpaRepository<Lemma, Integer> {

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE search_engine.lemma", nativeQuery = true)
    void truncate();

    long countBySiteId(Long site_id);

    @Query(value = "SELECT lemma FROM Lemma lemma WHERE lemma.siteId = :siteId AND lemma.lemma = :lemma")
    List<Lemma> findByLemmaAndSiteId(@Param("lemma")String lemma,@Param("siteId") Long siteId);


    @Query(value = "SELECT lemma FROM Lemma lemma WHERE lemma.siteId = :siteId AND lemma.lemma = :lemma")
    Lemma findByNameAndSiteId(@Param("lemma")String lemma,@Param("siteId") Long siteId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO lemma (frequency, lemma, site_id) VALUES (:frequency, :lemma, :siteId)" +
            "ON DUPLICATE KEY UPDATE frequency = frequency + :quantity", nativeQuery = true)
    int updateFrequency(@Param("frequency") Float frequency,@Param("lemma")String lemma,@Param("siteId")Long siteId,
                         @Param("quantity")Float quantity);
}
