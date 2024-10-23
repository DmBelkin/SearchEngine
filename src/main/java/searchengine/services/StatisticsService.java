package searchengine.services;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.utils.Observable;
import searchengine.config.SiteNode;
import searchengine.config.SitesList;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.model.*;
import searchengine.repository.LemmaRepo;
import searchengine.repository.PageRepo;
import searchengine.repository.SiteRepo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Setter
@Getter
public class StatisticsService implements Statistics {


    @Autowired
    private PageRepo pageRepo;

    @Autowired
    private SiteRepo siteRepo;

    @Autowired
    private LemmaRepo lemmaRepo;

    @Autowired
    private SitesList siteList;

    private StatisticsResponse response;

    private Observable observable;

    private List<SiteNode> sitesList;

    public StatisticsService() {
    }

    @Override
    public StatisticsResponse getStatistics() {
        TotalStatistics total = new TotalStatistics();
        total.setSites(siteList.getSites().size());
        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        sitesList = siteList.getSites();
        long fullPages = pageRepo.count();
        total.setIndexing(observable.isStarted());
        int lemmas = 0;
        for (int i = 0; i < sitesList.size(); i++) {
            SiteNode site = sitesList.get(i);
            DetailedStatisticsItem item = new DetailedStatisticsItem();
            item.setName(site.getName());
            item.setUrl(site.getUrl());
            Site site1 = siteRepo.findByName(site.getName());
            long pagesCount = 0;
            if (site1 != null) {
                long countLemmas = lemmaRepo.countBySiteId(site1.getId());
                item.setLemmas((int)countLemmas);
                lemmas += countLemmas;
                pagesCount = pageRepo.countBySiteId(site1.getId());
                if (site1.getStatus().equals(StatusValue.INDEXING)) {
                    item.setError(Errors.Идет_индексация.toString());
                    item.setStatus(StatusValue.INDEXING.toString());
                } else if (site1.getStatus().equals(StatusValue.FAILED)) {
                    item.setError(Errors.Запустите_индексацию.toString());
                    item.setStatus(StatusValue.FAILED.toString());
                } else if (site1.getStatus().equals(StatusValue.INDEXED)) {
                    item.setError(Errors.Проиндексировано.toString());
                    item.setStatus(StatusValue.INDEXED.toString());
                }
            } else {
                item.setStatus(StatusValue.FAILED.toString());
                item.setError("Сайт не индексировался");
            }
            item.setPages((int)pagesCount);
            item.setStatusTime(LocalDateTime.now());
            detailed.add(item);
        }
        total.setPages((int)fullPages);
        total.setLemmas(lemmas);
        StatisticsData statisticsData = new StatisticsData();
        statisticsData.setTotal(total);
        statisticsData.setDetailed(detailed);
        response.setStatistics(statisticsData);
        response.setResult(true);
        return response;
    }
}
