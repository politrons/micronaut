package micronaut.features.resources;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.runtime.context.scope.refresh.RefreshEvent;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import micronaut.features.model.Engineer;
import micronaut.features.model.Human;
import micronaut.features.model.RefreshableModel;
import micronaut.features.service.UserService;

import javax.inject.Inject;

@Controller("/micronaut")
public class FeatureController {

    @Inject
    private Engineer engineer;

    @Inject
    private RefreshableModel refreshableModel;

    @Inject
    private UserService userService;

    /**
     * Endpoint to prove IoC using Micronaut, which is really good since it´s working without use reflection
     * and make the dependency "ahead of time" and not "just in time" as normally Dependency injection of Spring does.
     */
    @Get("/ioc")
    public String getEngineerUsingIoC() {
        return "Hello " + engineer.getName();
    }

    /**
     * Get path params is so simple like combine the name {variable} in the path and in the method argument
     *
     * @param username as path param
     * @return the User in Json format
     */
    @Get("/user/{username}")
    public Human pathParams(String username) {
        return userService.findUserByName(username);
    }

    /**
     * Micronaut has an amazing integration with RxJava, it able to serialize a Single/Observable
     * without have to worry of subscription.
     * * @return a lazy observable that it will be evaluated by Micronaut eventually.
     * It will also allow run the observable async using [subscribeOn] operator
     */
    @Get("/rxJava/{name}")
    Observable<String> rxJava(String name) {
        return Observable.just("Hello " + name + " to reactive world in Micronaut")
                .filter(value -> value.contains("politrons"))
                .map(String::toUpperCase)
                .map(value -> value.concat("!!!!"))
                .map(value -> value.concat(" Request processed in Thread:" + Thread.currentThread().getName()))
                .subscribeOn(Schedulers.newThread());
    }

    /**
     * Here we use the scope @Refreshable which allow refresh an instance using /refresh endpoint or just
     * publishing an [RefreshEvent] in the applicationContext.
     * Really handy if you need to update the state of one of your dependencies under some circumstances.
     */
    @Get("/getCurrentTimeFromIoc")
    public String getCurrentTime() {
        return "Currrent time is " + refreshableModel.getTime();
    }

    @Get("/refreshTime")
    public void refreshTime() {
        ApplicationContext context = ApplicationContext.run();
        context.publishEvent(new RefreshEvent());
    }
}