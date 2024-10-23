
package searchengine.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
@Table(name = "indexes")
public class Indexes implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private Long id;

    @NotNull
    @Column(name = "page_id")
    private Long pageId;

    @NotNull
    private Long lemmaId;

    @NotNull
    private Float ranks;

}
