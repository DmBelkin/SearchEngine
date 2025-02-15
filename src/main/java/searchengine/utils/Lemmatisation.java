package searchengine.utils;

import lombok.Getter;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.repository.IndexRepo;
import searchengine.repository.LemmaRepo;

import java.util.*;

@Getter
public class Lemmatisation {

    private LuceneMorphology luceneMorphology;

    private LuceneMorphology engluceneMorphology;

    private Page page;

    private LemmaRepo lemmaRepo;

    private IndexRepo indexRepo;

    private Observable observable;

    private CalculateLemmaRankByPage calculateLemmaRankByPage;

    private LemmaTransaction lemmaTransaction;

    private String text;

    public Lemmatisation(LuceneMorphology luceneMorphology, LuceneMorphology engluceneMorphology,
                         LemmaRepo lemmaRepo, IndexRepo indexRepo, Observable observable) {
        this.luceneMorphology = luceneMorphology;
        this.engluceneMorphology = engluceneMorphology;
        this.lemmaRepo = lemmaRepo;
        this.indexRepo = indexRepo;
        this.observable = observable;
        calculateLemmaRankByPage = new CalculateLemmaRankByPage();
        lemmaTransaction = new LemmaTransaction(lemmaRepo, indexRepo, observable);
    }

    public Lemmatisation(LuceneMorphology luceneMorphology, LuceneMorphology engLuceneMorphology) {
        this.luceneMorphology = luceneMorphology;
        this.engluceneMorphology = engLuceneMorphology;
        calculateLemmaRankByPage = new CalculateLemmaRankByPage();
        lemmaTransaction = new LemmaTransaction(lemmaRepo, indexRepo, observable);
    }

    public void startLemmatisation(String content) {
        long start = System.currentTimeMillis();
        Map<String, Float> map = lemma(splitTextIntoWords(text), text, content);
        long end = System.currentTimeMillis();
        System.out.println("lemmatisation time: " + (end - start));
        long start1 = System.currentTimeMillis();
        transaction(map,
                page.getSiteId(),
                page.getId());
        page = null;
        text = "";
        long end1 = System.currentTimeMillis();
        System.out.println("transaction time: " + (end1 - start1));
        System.out.println("**************************************");
    }

    public void setPage(Page page, String text) {
        this.page = page;
        this.text = text;
    }

    public Map<String, Float> query(String searchQuery) {
        return lemma(splitTextIntoWords(searchQuery), searchQuery, searchQuery);
    }

    public synchronized void transaction(Map<String, Float> lemmas, Long siteId, Long pageId) {
        Map<Lemma, Float> lemmaMap = new HashMap<>();
        for (Map.Entry<String, Float> map : lemmas.entrySet()) {
            Lemma lemma = new Lemma();
            lemma.setId(0l);
            lemma.setLemma(map.getKey());
            lemma.setSiteId(siteId);
            lemma.setFrequency(map.getValue());
            lemmaMap.put(lemma, map.getValue());
        }
        lemmaTransaction.setAll(new TreeMap<>(lemmaMap), pageId);//  в отдельный поток?
    }

    public List<List<String>> splitTextIntoWords(String text) {
        text = text.replaceAll("[<>.,:;)(?!\"'0123456789/-]", "");
        String[] words = text.split("\\s+");
        List<String> rus = new ArrayList<>();
        List<String> eng = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            String substring = words[i];
            if (substring.length() < 2) {
                continue;
            }
            if (substring.matches("[A-Za-z]+")) {
                eng.add(substring.toLowerCase().trim());
            }
            if (substring.matches("[А-Яа-я]+")) {
                rus.add(substring.toLowerCase().trim());
            }
        }
        return new ArrayList<>() {{
            add(rus);
            add(eng);
        }};
    }

    public Map<String, Float> lemma(List<List<String>> text, String content, String fullContent) throws WrongCharaterException {
        Map<String, Float> map = new TreeMap<>();
        for (int i = 0; i < 2; i++) {
            List<String> list = text.get(i);
            for (String word : list) {
                List<String> morph;
                if (i == 0) {
                    morph = luceneMorphology.getMorphInfo(word);
                } else {
                    morph = engluceneMorphology.getMorphInfo(word);
                }
                String w = morph.get(0).trim();
                if (w.contains("СОЮЗ") || w.contains("ПРЕДЛ") || w.contains("МЕЖД")) {
                    continue;
                }
                String[] arr = w.split("\\|");
                String cleanText = arr[0];
                if (cleanText.equals("")) {
                    continue;
                }
                try {
                    if (i == 0) {
                        List<String> wordBaseForms =
                                luceneMorphology.getNormalForms(cleanText);
                        map.putAll(calculateRank(wordBaseForms, fullContent));
                    } else {
                        List<String> eng = engluceneMorphology.getNormalForms(cleanText);
                        map.putAll(calculateRank(eng, fullContent));
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }


    public Map<String, Float> calculateRank(List<String> lemmas, String content) {
        Map<String, Float> map = new HashMap<>();
        for (String lemma : lemmas) {
            map.put(lemma, calculateLemmaRankByPage.KMPCalculateRank(lemma, content));
        }
        return map;
    }
}
