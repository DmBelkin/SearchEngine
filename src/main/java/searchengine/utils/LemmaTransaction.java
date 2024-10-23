package searchengine.utils;

import lombok.*;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import searchengine.model.Indexes;
import searchengine.model.Lemma;
import searchengine.repository.IndexRepo;
import searchengine.repository.LemmaRepo;

import java.util.*;


@Getter
public class LemmaTransaction {


    private LemmaRepo lemmaRepo;

    private IndexRepo indexRepo;

    private Observable observable;


    public LemmaTransaction(LemmaRepo lemmaRepo, IndexRepo indexRepo, Observable observable) {
        this.lemmaRepo = lemmaRepo;
        this.indexRepo = indexRepo;
        this.observable = observable;

    }

    @CachePut
    @Cacheable(value = "lemmaCache")
    public Integer updateQuantityAndSave(float frequency, String lemma, Long siteId, Float quantity) {
        return lemmaRepo.updateFrequency(frequency, lemma, siteId, quantity);
    }

    public void setAll(Map<Lemma, Float> lemmas, Long pageId) {
        List<Indexes> indexes = new ArrayList<>();
        for (Map.Entry<Lemma, Float> m : lemmas.entrySet()) {
            Lemma lemma = m.getKey();
            updateQuantityAndSave(lemma.getFrequency(),
                    lemma.getLemma(), lemma.getSiteId(), m.getValue());
            Lemma lemma1 = lemmaRepo.findByNameAndSiteId(lemma.getLemma(), lemma.getSiteId());
            Indexes index = new Indexes();
            index.setLemmaId(lemma1.getId());
            index.setPageId(pageId);
            Float rank = lemmas.get(lemma);
            if (rank != null) {
                index.setRanks(rank);
            }
            indexes.add(index);
        }
        indexRepo.saveAll(indexes);
    }
}
