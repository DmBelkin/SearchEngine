package searchengine.model;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.lang.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "site")
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "enum('INDEXING', 'INDEXED', 'FAILED')")
    @NotNull
    private StatusValue status;

    @NotNull
    @Column(name = "status_time")
    private LocalDateTime statusTime;

    @Column(name = "last_error", columnDefinition = "text")
    @Nullable
    private String lastError;

    @NotNull
    @Column(columnDefinition = "varchar(255)")
    private String url;

    @NotNull
    @Column(columnDefinition = "varchar(255)")
    private String name;
}

