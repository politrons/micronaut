package micronaut.features.resources;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.version.annotation.Version;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
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
import micronaut.features.retry.RetryStrategy;
import micronaut.features.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

import static io.micronaut.http.HttpResponse.ok;

/**
 * Async Reactive programing with Micronaut:
 * If your controller method returns a non-blocking type such as an RxJava Observable or a CompletableFuture
 * then Micronaut will use the Event loop thread to subscribe to the result.
 * Here we cover both examples.
 */
@Controller("/micronaut")
public class FeatureController {

    private static final Logger logger = LoggerFactory.getLogger(FeatureController.class);

    @Inject
    private Engineer engineer;

    @Inject
    private RefreshableModel refreshableModel;

    @Inject
    private UserService userService;

    @Inject
    private RetryStrategy retryStrategy;

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
     * Endpoint to show how AOP works with micronaut.
     * For this example since the invocation of the method it might throw an exception
     * is mandatory for the shake of the program to wrap in the Monad Try of Vavr.
     * When we invoke the method [checkIfIsUppercase] since it has the annotation
     * [IsUpperCase] it will be intercept by the interceptor [IsUpperCaseValue]
     */
    @Get("/aop/{value}")
    public String checkIfIsUpperCase(String value) {
        Try<String> response = Try.of(() -> checkIfIsUppercase(value));
        if (response.isSuccess()) {
            return response.get();
        }
        return response.failed().get().getMessage();
    }

    @Get("/retryStrategy")
    public String retryStrategy() {
        Try<String> response = Try.of(() -> retryStrategy.getValue());
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
     * Micronaut Http server also provide the possibility to manage with the request/response
     * our self we just need to expect [HttpRequest] and return [HttpResponse]
     */
    @Get("/requestResponse")
    HttpResponse<String> requestResponse(HttpRequest<?> request) {
        String name = request.getParameters()
                .getFirst("name")
                .orElse("Nobody");
        return ok("Hello " + name + "!!")
                .header("X-My-Header", "Foo");
    }

    /**
     * In Micronaut, in order to have API versioning, you have to enable and configure in your application.yml.
     * Once is configured by Header or parameter the way the argument Version is passed, you can have multiple
     * endpoints with same address in your resources.
     * In our example we pass the header [X-API-VERSION] key with value [1 || 2]
     */
    @Version("1")
    @Get("/apiVersioned")
    String apiV1() {
        logger.info("Endpoint version 1 reached");
        return "hello world V1";
    }

    @Version("2")
    @Get("/apiVersioned")
    String apiv2() {
        logger.info("Endpoint version 2 reached");
        return "hello world V2";
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