package micronaut.features.filter;


import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import micronaut.features.retry.impl.RetryStrategyImpl;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement filters in Micronaut it never was so simple.
 * We just need to implement [HttpServerFilter] which follow the Jax-RS standard filter [doFilter]
 * There we need to proceed with the [chain] once we are ready, but the signature of the
 * method since it´s not blocking and reactive, it force us to implement a rx publisher.
 * Here using [Flowable] fron rxjava2 we can create a publisher, and using [subscribeOn] as
 * usual we can make the publisher run async.
 *
 * Now how to make the endpoints being affected by the filter is even simpler. Using just
 * the filter in the annotation [Filter] we tell the service that all endpoints under [/micronaut/]
 * they will be affected by this filter
 */
@Filter("/micronaut/**")
public class RequestTraceFilter implements HttpServerFilter {

    private static final Logger logger = LoggerFactory.getLogger(RetryStrategyImpl.class);

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        return Flowable.just(request)
                .doOnNext(r -> logger.info("Request to endpoint " + r.getUri() + " filter"))
                .switchMap(r -> chain.proceed(request)).subscribeOn(Schedulers.io());

    }
}