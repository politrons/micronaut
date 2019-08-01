
# ![My image](src/main/resources/img/logo.png)

A modern, JVM-based, full-stack framework for building modular, easily testable microservice and serverless applications.

### Run application

* Create the jar

```
./gradlew assemble
```

* run the Java process 
```
java -jar build/libs/micronaut-features-0.1.jar
```

### Features

* Examples of Rest endpoints [Here](src/main/java/micronaut/features/resources/FeatureController.java)

* Example of how IoC works [Here](src/main/java/micronaut/features/model)

* Example of how AOP works [here](src/main/java/micronaut/features/aop/NotNullInterceptor.java)