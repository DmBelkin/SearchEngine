package searchengine.model;

import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "page", indexes = @Index(columnList = "path"))
public class Page implements Serializable, Comparable<Page> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private Long id;

    @NotNull
    @Column(name = "site_id")
    private Long siteId;

    @NotNull
    @Column(name = "path", columnDefinition = "varchar(250)")
    private String path; // indexation

    @NotNull
    private int code;

    @NotNull
    @Column(columnDefinition = "mediumtext")
    private String content;

    @Override
    public String toString() {
        return "" + id;
    }

    @Override
    public int compareTo(Page o) {
        return Long.compare(o.id, this.id);
    }

    public boolean equals(Page o) {
        if (o == this) {
            return true;
        }
        return this.id == o.id;
    }
}

