package micronaut.features.service.impl;

import micronaut.features.model.Human;
import micronaut.features.service.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserServiceImpl implements UserService {

    @Inject
    Human human;

    @Override
    public Human findUserByName(String name) {
        return human;
    }
}
