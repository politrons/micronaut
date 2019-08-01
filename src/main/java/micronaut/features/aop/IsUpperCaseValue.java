package micronaut.features.aop;

import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;

import javax.inject.Singleton;

/**
 * Silly example of how Aspect oriented programing [AOP] Work with Micronaut
 * This interceptor get the access to the method and check if the argument passed
 * to the method is a String in upper case
 */
@Singleton
public class IsUpperCaseValue implements MethodInterceptor<Object, Object> {
    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        boolean isNotUpperCase = context.getParameters()
                .values()
                .stream()
                .filter(argValue -> argValue.getValue() instanceof String)
                .anyMatch(argValue -> ((String) argValue.getValue()).chars()
                        .mapToObj(i -> (char) i)
                        .anyMatch(c -> !Character.isUpperCase(c)));
        if (isNotUpperCase) {
            throw new IllegalArgumentException("Argument passed to method must be String in upper case");
        } else {
            return context.proceed();
        }
    }
}