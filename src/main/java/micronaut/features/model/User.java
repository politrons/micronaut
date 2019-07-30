package micronaut.features.model;


import javax.inject.Singleton;

/**
 * JSR annotation to get a unique instance of the class
 * This scope make the instance unique in your whole program.
 */
@Singleton
public class User implements Human {

    @Override
    public String getName() {
        return "Paul";
    }

}
