package searchengine.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.model.Page;
import searchengine.model.Site;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class SiteCrawler extends RecursiveAction {


    private IndexationService service;

    private String url;

    private Site site;

    public SiteCrawler(IndexationService service, String url, Site site) {
        this.service = service;
        this.url = url;
        this.site = site;
    }

    @Override
    public void compute() {
        List<SiteCrawler> taskList = new ArrayList<>();
        try {
            Document document = Jsoup.connect(url).userAgent("Mozilla/5.0" +
                    " (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)" +
                    " Chrome/116.0.0.0 Safari/537.36").get();
            Elements elements = document.select("a");
            for (Element element : elements) {
                String url = element.absUrl("href");
                String[] parseWidth = element.attr("href").split("/");
                if (!url.startsWith(this.url) || service.getLinksCash().contains(url) ||
                        url.contains("#") || parseWidth.length > 6) {
                    continue;
                }

                System.out.println(url);
                Page page = new Page();
                page.setPath(element.attr("href"));
                page.setSiteId(site.getId());
                page.setContent(element.toString());
                service.setLinkSet(page);
                service.setIntoCash(url);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (taskList.size() > 252) {
                    joiner(taskList);
                    taskList.clear();
                }
                SiteCrawler crawler = new SiteCrawler(service, url, site);
                crawler.fork();
                taskList.add(crawler);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        joiner(taskList);
    }

    public void joiner(List<SiteCrawler> taskList) {
        for (SiteCrawler crawler : taskList) {
            crawler.join();
        }
    }

    public Site getSite() {
        return site;
    }

}


