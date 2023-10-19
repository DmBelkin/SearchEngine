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
@Table(name = "page", indexes = @Index(columnList = "path", unique = true))
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private int id;

    @NotNull
    @Column(name = "site_id")
    private int siteId;

    @NotNull
    @Column(name = "path", columnDefinition = "text")
    private String path; // indexation

    @NotNull
    private int code;

    @NotNull
    @Column(columnDefinition = "mediumtext")
    private String content;

}

