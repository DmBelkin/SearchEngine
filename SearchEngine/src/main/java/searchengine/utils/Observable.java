package searchengine.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class Observable {

    private volatile boolean isStarted = false;


}
