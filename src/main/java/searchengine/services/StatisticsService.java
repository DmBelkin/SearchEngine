package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.SiteNode;
import searchengine.config.SitesList;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.model.StatusValue;
import searchengine.repo.PageRepo;
import searchengine.repo.SiteRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StatisticsService {

    @Autowired
    SiteRepo siteRepo;

    @Autowired
    SitesList sitesList;

    @Autowired
    PageRepo pageRepo;


    public StatisticsResponse getResponse() {
        List<SiteNode> list = sitesList.getSites();
        Iterable<Site> sites = siteRepo.findAll();
        TotalStatistics total = new TotalStatistics();
        total.setSites(list.size());
        StatisticsResponse response = new StatisticsResponse();
        StatisticsData data = new StatisticsData();
        List<DetailedStatisticsItem> items = new ArrayList<>();
        for (Site site : sites) {
            DetailedStatisticsItem item = new DetailedStatisticsItem();
            item.setName(site.getName());
            item.setUrl(site.getUrl());
            item.setStatus(site.getStatus().toString());
            item.setStatusTime(site.getStatusTime());
            Optional<Page> pages = pageRepo.findById(site.getId());
            item.setPages((int) pages.stream().count());
        }
        data.setTotal(total);
        data.setDetailed(items);
        response.setStatistics(data);
        response.setResult(true);
        return response;
    }
}

