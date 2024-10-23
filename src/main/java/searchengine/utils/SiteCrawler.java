package searchengine.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.services.IndexationService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;


@Getter
@Setter
@AllArgsConstructor
public class SiteCrawler extends RecursiveAction {


    private IndexationService service;

    private String url;

    private Site site;

    private Observable observable;

    @Override
    public void compute() {
        List<SiteCrawler> taskList = new ArrayList<>();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        try {
            Document document = Jsoup.connect(url).userAgent("Mozilla/5.0" +
                    " (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)" +
                    " Chrome/116.0.0.0 Safari/537.36").referrer("http://www.google.com").get();
            Page page = new Page();
            page.setSiteId(site.getId());
            page.setContent(document.toString());
            page.setPath(url);
            String text = document.text().length() > 2000? document.text().substring(0, 2000): document.text(); ;
            Page page1 = service.setLinkSet(page, document.title() + " " + text);
            if (page1 == null) {
                throw new IllegalArgumentException();
            }
            Elements elements = document.select("a");
            for (Element element : elements) {
                String url = element.absUrl("href");
                if (!url.startsWith(site.getUrl()) || url.equals(site.getUrl()) ||
                        url.contains("#") || url.contains(".pdf") || url.endsWith(".txt") ||
                        url.endsWith(".jpg") || url.contains("*") || url.endsWith(".png") || url.endsWith(".JPG")
                        || url.endsWith(".zip") || url.endsWith(".sql") || url.endsWith(".webp")) {
                    continue;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (observable.isStarted()) {
                    SiteCrawler crawler = new SiteCrawler(service, url, site, observable);
                    crawler.fork();
                    taskList.add(crawler);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        join(taskList);
    }

    public void join(List<SiteCrawler> taskList) {
        for (SiteCrawler crawler : taskList) {
            crawler.join();
        }
    }
}


