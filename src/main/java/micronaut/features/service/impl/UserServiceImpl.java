package micronaut.features.service.impl;

import micronaut.features.model.Human;
import micronaut.features.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserServiceImpl implements UserService {


    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Inject
    private Human human;

    /**
     * Using micronaut we can use standard javax annotation @PostConstruct to be ivoked
     * after instantiate the bean.
     */
    @PostConstruct
    public void init() {
        logger.info("JavaX Post construct included in DI of Micronaut");
    }

    @Override
    public Human findUserByName(String name) {
        return human;
    }
}
