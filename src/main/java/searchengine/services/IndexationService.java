package searchengine.services;

import lombok.Getter;
import lombok.Setter;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.utils.Observable;
import searchengine.config.SiteNode;
import searchengine.config.SitesList;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.model.StatusValue;
import searchengine.repository.IndexRepo;
import searchengine.repository.LemmaRepo;
import searchengine.repository.PageRepo;
import searchengine.repository.SiteRepo;
import searchengine.utils.CrawlingStarter;
import searchengine.utils.Lemmatisation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Getter
@Setter
public class IndexationService implements Statistics {


    @Autowired
    private PageRepo pageRepo;

    @Autowired
    private SiteRepo siteRepo;

    @Autowired
    private LemmaRepo lemmaRepo;

    @Autowired
    private IndexRepo indexRepo;

    @Autowired
    private SitesList sitesList;

    private Observable observable;

    private Set<Page> pageSet;

    private List<CrawlingStarter> threads;

    private LuceneMorphology luceneMorphology;

    private StatisticsResponse response;

    private LuceneMorphology engluceneMorphology;

    private Lemmatisation lemmatisation;


    public IndexationService() {
        threads = new ArrayList<>();
        try {
            luceneMorphology = new RussianLuceneMorphology();
            engluceneMorphology = new EnglishLuceneMorphology();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void update(Site site) {
        site.setStatusTime(LocalDateTime.now());
        site.setStatus(StatusValue.INDEXING);
        siteRepo.save(site);
        listSites(new ArrayList<>() {{
            add(new SiteNode(site.getUrl(), site.getName()));
        }});
        observable.setStarted(true);
    }


    public void dropAndStart() {
        if (observable.isStarted()) {
            response.setResult(false);
            response.setError("Индексация уже запущена");
            return;
        }
        pageRepo.truncate();
        siteRepo.truncate();
        lemmaRepo.truncate();
        indexRepo.truncate();
        for (SiteNode node : sitesList.getSites()) {
            Site site = new Site();
            site.setName(node.getName());
            site.setUrl(node.getUrl());
            site.setStatusTime(LocalDateTime.now());
            site.setStatus(StatusValue.INDEXING);
            siteRepo.save(site);
        }
        observable.setStarted(true);
        response.setResult(true);
        response.setError("Индексация запущена");
        listSites(sitesList.getSites());
    }

    @Async
    public void listSites(List<SiteNode> list) {
        lemmatisation = new Lemmatisation(luceneMorphology,
                engluceneMorphology, lemmaRepo, indexRepo, observable);
        for (SiteNode node : list) {
            Site site = siteRepo.findByName(node.getName());
            CrawlingStarter starter = new CrawlingStarter(site, this, observable, siteRepo);
            starter.start();
            site.setStatus(StatusValue.INDEXING);
            site.setStatusTime(LocalDateTime.now());
            siteRepo.save(site);
            threads.add(starter);
        }
    }

    @Cacheable(value = "pageCache")
    public synchronized Page setLinkSet(Page page, String text) {
        Page page1 = pageRepo.findBypath(page.getPath());
        if (page1 != null) {
            return null;
        }
        page = pageRepo.save(page);
        insert(page, text);
        return page;
    }


    public synchronized void insert(Page page, String text) {
        lemmatisation.setPage(page, text);
        lemmatisation.startLemmatisation();
    }

    @Async
    public void stopIndexing() {
        if (!observable.isStarted()) {
            response.setResult(false);
            response.setError("Индексация не запущена");
        }
        observable.setStarted(false);
        response.setError("Индексация остановлена");
        response.setResult(true);
    }

    @Override
    public StatisticsResponse getStatistics() {
        return response;
    }
}

