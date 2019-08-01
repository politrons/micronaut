package micronaut.features.resources;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.runtime.context.scope.refresh.RefreshEvent;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.vavr.control.Try;
import micronaut.features.aop.IsUpperCase;
import micronaut.features.model.Engineer;
import micronaut.features.model.Human;
import micronaut.features.model.RefreshableModel;
import micronaut.features.service.UserService;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

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
                .flatMap(value -> Observable.just(value.concat("!!!!")))
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

    /**
     * Another great feature of Micronaut is that is completely async and allow return in the resource
     * a [CompletableFuture] allowing run your program async and dont have to worry to render the result
     * once it complete.
     */
    @Get("/completableFuture")
    public CompletableFuture<String> getResponseAsync() {
        return CompletableFuture.supplyAsync(() -> "Hello Async Java world")
                .thenApply(String::toUpperCase)
                .thenCompose(value -> CompletableFuture.supplyAsync(() -> value + "!!!"));
    }

    /**
     *
     */
    @Get("/aop/{value}")
    public String checkIfIsUpperCase(String value) {
        Try<String> response = Try.of(() -> checkIfIsUppercase(value));
        if (response.isSuccess()) {
            return response.get();
        }
        return response.failed().get().getMessage();
    }


    @Get("/refreshTime")
    public void refreshTime() {
        ApplicationContext context = ApplicationContext.run();
        context.publishEvent(new RefreshEvent());
    }

    /**
     * Using the annotation that we create we will make the invocation being previously
     * got by [IsUpperCaseValue] interceptor class and method [intercept] if everything
     * is fine, the interceptor it will proceed to this method, otherwise it will rise an
     * Exception back.
     */
    @IsUpperCase
    public String checkIfIsUppercase(String value) {
        return value + " WORKS";
    }
}