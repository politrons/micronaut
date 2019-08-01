package micronaut.features.model;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * JSR annotation to get a unique instance of the class
 * This scope make the instance unique in your whole program.
 */
@Singleton
public class Engineer {

    /**
     * Micronaut IoC it will use the javax Inject annotation to inject the dependency in this entity.
     */
    @Inject
    Human human;

    public String getName() {
        return human.getName();
    }
}
