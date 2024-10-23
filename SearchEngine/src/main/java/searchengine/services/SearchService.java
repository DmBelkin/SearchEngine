package searchengine.services;

import lombok.Setter;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.dto.search.Response;
import searchengine.dto.search.SearchResult;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.model.Indexes;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repository.IndexRepo;
import searchengine.repository.LemmaRepo;
import searchengine.repository.PageRepo;
import searchengine.repository.SiteRepo;
import searchengine.utils.Lemmatisation;

import java.io.IOException;
import java.util.*;

@Service
@Setter
public class SearchService implements Statistics {

    private String query;

    private HashMap<String, Integer> lemmas;

    private Lemmatisation lemmatisation;

    @Autowired
    private SiteRepo siteRepo;

    @Autowired
    private PageRepo pageRepo;

    @Autowired
    private LemmaRepo lemmaRepo;

    @Autowired
    private IndexRepo indexRepo;

    private Response response;

    private int offset;

    private int limit;


    public SearchService() {
        lemmas = new HashMap<>();
        try {
            LuceneMorphology luceneMorphology = new RussianLuceneMorphology();
            LuceneMorphology engLuceneMorphology = new EnglishLuceneMorphology();
            lemmatisation = new Lemmatisation(luceneMorphology, engLuceneMorphology);
            response = new Response();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setQuery(String query, String url) {
        if (url.isBlank()) {
            response.setResult(false);
            response.setError("Сайт не найден");
            return;
        } else if (query.isBlank()) {
            response.setResult(false);
            response.setError("Введите запрос");
            return;
        }
        Site site = siteRepo.findByurl(url);
        if (site == null) {
            response.setResult(false);
            response.setError("Сайт не найден");
        } else {
            this.query = query;
            Map<String, Float> map = lemmatisation.query(query);
            if (!map.isEmpty()) {
                makeSnippets(map, site, query.length());
            }
            this.lemmas.clear();
        }
    }


    public void makeSnippets(Map<String, Float> map, Site site, int length) {
        List<SearchResult> resultList = new ArrayList<>();
        List<Lemma> lemmaList = new ArrayList<>();
        for (Map.Entry<String, Float> m : map.entrySet()) {
            List<Lemma> lemmas = lemmaRepo.findByLemmaAndSiteId(m.getKey(), site.getId());
            lemmaList.addAll(lemmas);
        }
        if (lemmaList.isEmpty()) {
            response.setResult(true);
            response.setCount(0);
            response.setData(new ArrayList<>());
            return;
        }
        Comparator<Lemma> comparator = Comparator.comparing(o -> o.getFrequency());
        Collections.sort(lemmaList, comparator);
        List<Indexes> index = indexRepo.findAllByLemmaId(lemmaList.get(0).getId());
        Map<Page, Float> pages = new HashMap<>();
        float maxRelevance = 1;
        for (Indexes indexes : index) {
            maxRelevance += indexes.getRanks();
        }
        for (Indexes indexes : index) {
            Optional<Page> page = pageRepo.findById(indexes.getPageId());// запросить пакетом через репозиторий
            if (page.isPresent()) {
                Page page1 = page.get();
                Float n = pages.get(page1);
                if (n == null) {
                    pages.put(page1,indexes.getRanks() / maxRelevance);
                } else {
                    pages.put(page1, n + (indexes.getRanks() / maxRelevance));
                }
            }
        }
        for (Map.Entry<Page, Float> m : pages.entrySet()) {
            Page page = m.getKey();
            Document document = Jsoup.parse(page.getContent());
            String title = document.title();
            String text = document.text().length() > 2000? document.text().substring(0, 2000): document.text();
            String c = title + " " + text;
            c = c.replaceAll("[%+<>.,:;)(?!\"'/0123456789-]", "");
            String snippet = "";
            float relevation = m.getValue();
            SearchResult result = new SearchResult();
            result.setUri(page.getPath());
            boolean containsAllLemmas = true;
            for (int i = 0; i < lemmaList.size(); i++) {
                String subSnippet = lemmaSearch(lemmaList.get(i).getLemma(), c.toLowerCase(), c.toLowerCase());
                if (!subSnippet.isBlank() && snippet.length() <= 300) {
                    snippet += subSnippet;
                }
                if (subSnippet.isBlank()) {
                    containsAllLemmas = false;
                    break;
                }
            }
            if (!containsAllLemmas) {
                continue;
            }
            if (snippet.isBlank()) {
                snippet = title;
            }
            result.setSite("");
            result.setSnippet(snippet);
            result.setTitle(title);
            result.setUri(page.getPath());
            result.setSiteName(site.getName());
            result.setRelevance(relevation);
            resultList.add(result);
        }
        Comparator<SearchResult> comp = Comparator.comparing(o -> o.getRelevance());
        Collections.sort(resultList, comp);
        Collections.reverse(resultList);
        response.setResult(true);
        response.setCount(resultList.size());
        response.setData(resultList);
    }


    public String lemmaSearch(String p, String s, String content) {
        String snippet = "";
        int m = p.length();
        int n = s.length();
        int[] lps = computePrefix(s);
        int j = 0;
        int i = 0;
        while (i < n) {
            if (p.charAt(j) == s.charAt(i)) {
                j++;
                i++;
            }
            if (m - j == 0) {
                int start = i - j;
                int end = (i - j) + p.length();
                if (end + 40 <= content.length() - 1) {
                    snippet += "<b>" + content.substring(start, end) + "<b>" + content.substring(end, end + 40) + "\s";
                } else {
                    snippet += "<b>" + content.substring(start, end) + "<b>" + content.substring(end) + "\s";
                }
                j = lps[j - 1];
            } else if (i < n && p.charAt(j) != s.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i = i + 1;
                }
            }
        }
        return snippet;
    }

    public int[] computePrefix(String input) {
        int[] prefix = new int[input.length()];
        int j;
        for (int i = 1; i < input.length(); i++) {
            j = prefix[i - 1];
            while (j > 0 && input.charAt(j) != input.charAt(i)) {
                j = prefix[j - 1];
            }
            if (input.charAt(j) == input.charAt(i)) {
                j += 1;
            }
            prefix[i] = j;
        }
        return prefix;
    }

    @Override
    public StatisticsResponse getStatistics() {
        return response;
    }
}
