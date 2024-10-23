package searchengine.dto.search;

import lombok.Data;
import searchengine.dto.statistics.StatisticsResponse;

import java.util.List;

@Data
public class Response extends StatisticsResponse
{
    private boolean result;
    private int count;
    private String error;
    private List<SearchResult> data;

}
