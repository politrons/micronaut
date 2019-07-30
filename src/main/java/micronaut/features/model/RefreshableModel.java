package micronaut.features.model;


import io.micronaut.runtime.context.scope.Refreshable;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This scope make the model being refreshed and in this case invoke @PostConstruct every time
 * the endpoint /refresh is invoked in your application
 */
@Refreshable
public class RefreshableModel {
    private String time;

    @PostConstruct
    public void init() {
        time = new SimpleDateFormat("dd/MMM/yy HH:mm:ss.SSS").format(new Date());
    }

    public String getTime() {
        return time;
    }
}