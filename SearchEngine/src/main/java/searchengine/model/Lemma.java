package searchengine.model;

import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
@Table(name = "lemma", uniqueConstraints = {@UniqueConstraint(name = "lemma_by_site", columnNames = {
        "site_id", "lemma"})})
public class Lemma implements Serializable, Comparable<Lemma> {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigint")
    private Long id;

    @NotNull
    @Column(name = "site_id")
    private Long siteId;

    @NotNull
    @Column(name = "lemma", columnDefinition = "varchar(100)")
    private String lemma;

    @NotNull
    private Float frequency;

    @Override
    public int compareTo(Lemma o) {
        if (this.id == o.id && id != 0) {
            return 0;
        } else if (o.getLemma().equals(this.getLemma()) && this.siteId == o.siteId) {
            return 0;
        }
        return o.lemma.compareTo(this.lemma);
    }
}


