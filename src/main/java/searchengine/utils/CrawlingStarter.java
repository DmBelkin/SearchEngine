package searchengine.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import searchengine.config.SiteNode;
import searchengine.model.Site;
import searchengine.model.StatusValue;
import searchengine.repository.SiteRepo;
import searchengine.services.IndexationService;

import java.time.LocalDateTime;
import java.util.concurrent.ForkJoinPool;

@AllArgsConstructor
@Getter
public class CrawlingStarter extends Thread {

    private Site indexingSite;

    private IndexationService service;

    private Observable observable;

    private SiteRepo repo;

    public void run() {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        SiteCrawler crawler = new SiteCrawler(service, indexingSite.getUrl(), indexingSite, observable);
        forkJoinPool.invoke(crawler);
        while(true) {
            if (forkJoinPool.isTerminated()) {
                indexingSite.setStatus(StatusValue.INDEXED);
                indexingSite.setStatusTime(LocalDateTime.now());
                repo.save(indexingSite);
                forkJoinPool.shutdown();
                this.stop();
                break;
            }
            if (!observable.isStarted()) {
                indexingSite.setStatus(StatusValue.FAILED);
                indexingSite.setStatusTime(LocalDateTime.now());
                repo.save(indexingSite);
                forkJoinPool.shutdown();
                this.stop();
                break;
            }
        }
    }
}
