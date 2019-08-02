
# ![My image](src/main/resources/img/logo.png)

A modern, JVM-based, full-stack framework for building modular, easily testable microservice and serverless applications.

You can find the official documentation [here](https://docs.micronaut.io/latest/guide/index.html#apiVersioning) 

### Features

* Examples of Rest endpoints(Reactive return, Versioning, Path/Query param) [Here](src/main/java/micronaut/features/resources/FeatureController.java)
* Example of how IoC works [Here](src/main/java/micronaut/features/model)
* Example of how AOP works [here](src/main/java/micronaut/features/aop/NotNullInterceptor.java)
* Example of how Retry strategy works [here](src/main/java/micronaut/features/retry/impl/RetryStrategyImpl.java)

### Run application

* Create the jar

```
./gradlew assemble
```

* run the Java process 
```
java -jar build/libs/micronaut-features-0.1.jar
```
