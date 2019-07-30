package micronaut.features.resources;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/second")
public class SecondController {

    @Get(produces = MediaType.TEXT_PLAIN)
    public String index() {
        return "War of the Worlds";

    }
}