package searchengine.config;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SiteNode {
    private String url;
    private String name;
}
