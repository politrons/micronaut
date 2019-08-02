package micronaut.features.retry.impl;

import io.micronaut.retry.annotation.Retryable;
import micronaut.features.retry.RetryStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

/**
 * In order to have Retry strategy in Micronaut is so simple like use [Retryable] annotation
 * it also provide the option to introduce the number of attempts and the delay backoff time
 * between every retry.
 */
@Singleton
public class RetryStrategyImpl implements RetryStrategy {

    private static final Logger logger = LoggerFactory.getLogger(RetryStrategyImpl.class);

    @Override
    @Retryable(attempts = "3", delay = "1s")
    public String getValue() {
        double random = Math.random();
        if (random < 0.5) {
            logger.error("Error trying to get the value.");
            throw new IllegalArgumentException("Error getting the value");
        }
        return "Succeed Response";
    }
}
