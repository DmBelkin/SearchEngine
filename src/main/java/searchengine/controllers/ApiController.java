package searchengine.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.AddSiteService;
import searchengine.services.IndexationService;
import searchengine.utils.Observable;
import searchengine.services.SearchService;
import searchengine.services.StatisticsService;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ApiController {


    /**
     * write observer
     */

    @Autowired
    private IndexationService indexationService;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private AddSiteService addSiteService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private Observable observable;

    @Autowired
    private StatisticsResponse response;

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        statisticsService.setObservable(observable);
        statisticsService.setResponse(response);
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<StatisticsResponse> start() throws InterruptedException {
        System.out.println("on");
        indexationService.setResponse(response);
        indexationService.setObservable(observable);
        indexationService.dropAndStart();
        Thread.sleep(100);
        return ResponseEntity.ok(indexationService.getStatistics());
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<StatisticsResponse> stop() throws InterruptedException {
        System.out.println("off");
        indexationService.setResponse(response);
        indexationService.setObservable(observable);
        indexationService.stopIndexing();
        Thread.sleep(100);
        return ResponseEntity.ok(indexationService.getStatistics());
    }

    @PostMapping("/indexPage")
    public ResponseEntity<StatisticsResponse> addSite(@RequestParam(value = "url") String url)
            throws InterruptedException, IOException {
        addSiteService.setResponse(response);
        addSiteService.setUrl(url);
        addSiteService.setIndexationService(indexationService);
        addSiteService.add();
        Thread.sleep(100);
        return ResponseEntity.ok(addSiteService.getStatistics());
    }

    @GetMapping("/search")
    public ResponseEntity<StatisticsResponse> search(@RequestParam(value = "query") String query,
                                                     @RequestParam(value = "site") String site,
                                                     @RequestParam(value = "offset") int offset,
                                                     @RequestParam(value = "limit") int limit) {
        searchService.setQuery(query, site);
        return ResponseEntity.ok(searchService.getStatistics());
    }
}


