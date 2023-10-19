
package searchengine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "indexes")
public class Indexes {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private int id;

    @NotNull
    @Column(name = "page_id")
    private int pageId;

    @NotNull
    @Column(name = "lemma_id")
    private int lemmaId;

    @NotNull
    private float ranks;
}
