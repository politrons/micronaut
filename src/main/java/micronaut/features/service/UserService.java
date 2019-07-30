package micronaut.features.service;

import micronaut.features.model.Human;

public interface UserService {

    Human findUserByName(String name);
}
