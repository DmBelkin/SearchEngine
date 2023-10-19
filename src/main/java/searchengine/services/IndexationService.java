package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.SiteNode;
import searchengine.config.SitesList;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.model.StatusValue;
import searchengine.repo.PageRepo;
import searchengine.repo.SiteRepo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ForkJoinPool;

@Service
public class IndexationService {


    @Autowired
    private PageRepo pageRepo;

    @Autowired
    private SiteRepo siteRepo;

    @Autowired
    private SitesList list;

    private Set<String> linksCash;

    private Set<Page> pageSet;

    private List<SiteCrawler> threads;

    public IndexationService() {
        pageSet = new CopyOnWriteArraySet<>();
        linksCash = new CopyOnWriteArraySet<>();
        threads = new ArrayList<>();
    }

    public void drop() {
       pageRepo.truncate();
       siteRepo.truncate();
    }

    public void listSites() {
        //for (int i = 0; i <= list.getSites().size() - 1; i++) {
        indexingStarter(list.getSites().get(0), 0);
        //обход сайтов из конфига и запуск индексации
        //}
    }

    public void indexingStarter(SiteNode site, int siteNumber) {
        Site indexingSite = new Site();
        indexingSite.setName(site.getName());
        indexingSite.setUrl(site.getUrl());
        indexingSite.setStatus(StatusValue.INDEXING);
        indexingSite.setStatusTime(LocalDateTime.now());
        siteRepo.save(indexingSite);
        //indexingSite = siteRepo.findAll().get(siteNumber);
        SiteCrawler crawler = new SiteCrawler(this, site.getUrl(), indexingSite);
        var pool =  new ForkJoinPool().invoke(crawler);
        //закинуть потоки в лист
    }

    public synchronized void setLinkSet(Page page) {
        pageSet.add(page);
        if (pageSet.size() % 1000 == 0) {
            insert(new HashSet<>(pageSet));
            pageSet.clear();
            // + лемматизация
        }
    }

    public Set<Page> getPageSet() {
        return pageSet;
    }

    public void insert(Set<Page> set) {
        pageRepo.saveAll(set);
    }

    public Set<String> getLinksCash() {
        return linksCash;
    }

    public  void setIntoCash(String uri) {
        linksCash.add(uri);
    }

    public void stopIndexing() {
        for (SiteCrawler crawler : threads) {
            if (!crawler.isDone()) {
                crawler.join();
                Site site = crawler.getSite();
                site.setStatus(StatusValue.FAILED);
                site.setStatusTime(LocalDateTime.now());
                siteRepo.save(site);
            }
        }
    }
}

