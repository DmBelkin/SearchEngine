package searchengine.services;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.SiteNode;
import searchengine.config.SitesList;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.model.Site;
import searchengine.model.StatusValue;
import searchengine.repository.SiteRepo;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Getter
@Setter
public class AddSiteService implements Statistics {

    @Autowired
    private SitesList sitesList;

    @Autowired
    SiteRepo siteRepo;

    private final File configPath = new File("src/main/resources/application.yaml");

    private IndexationService indexationService;

    private String url;

    private StatisticsResponse response;


    public void add() throws IOException {
        if (contains(url) && uriValidate(url)) { // проверить
            Site site = siteRepo.findByurl(url);
            indexationService.update(site);
            response.setResult(true);
            response.setError("Сайт переиндексируется");
        } else if (uriValidate(url)) {
            FileWriter writer = new FileWriter(configPath, true);
            String name = url.substring(8);
            String config = "\n\s\s\s\s- url: " + url + "\n" +
                    "\s\s\s\s\s\sname: " + name;
            writer.write(config);
            writer.close();
            SiteNode node = new SiteNode(url, name);
            indexationService.getSitesList().getSites().add(node);
            Site site = new Site();
            site.setName(name);
            site.setUrl(url);
            site.setStatus(StatusValue.FAILED);
            site.setStatusTime(LocalDateTime.now());
            siteRepo.save(site);
            response.setResult(true);
            response.setError("Сайт добавлен");
        } else {
            response.setResult(false);
            response.setError("Сайт не добавлен, проверьте ссылку");
        }
    }

    public boolean contains(String uri) {
        for (SiteNode node : sitesList.getSites()) {
            if (node.getUrl().equals(uri)) {
                return true;
            }
        }
        return false;
    }

    public boolean uriValidate(String uri) {
        if (uri.isBlank()) {
            return false;
        }
        String uriRegex = "^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}[\\.][a-z\\/]{2,3}";
        Pattern pattern = Pattern.compile(uriRegex);
        Matcher matcher = pattern.matcher(uri);
        if(!matcher.matches()) {
            return false;
        }
         return true;
    }

    @Override
    public StatisticsResponse getStatistics() {
        return response;
    }
}
