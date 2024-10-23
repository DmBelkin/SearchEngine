package searchengine.repository;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.repository.configuration.RedisRepositoriesRegistrar;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Site;


@Repository
public interface SiteRepo extends JpaRepository<Site, Integer> {

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE search_engine.site", nativeQuery = true)
    void truncate();

    Site findByName(String name);

    Site findByurl(String url);
}


