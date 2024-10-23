package searchengine.dto.statistics;

import lombok.*;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Component
public class StatisticsResponse {
    private boolean result;
    private StatisticsData statistics;
    private String error;
}
