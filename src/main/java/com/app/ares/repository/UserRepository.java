package com.app.ares.repository;

import com.app.ares.domain.User;

import java.util.Collection;

public interface UserRepository <T extends User>{

    T create(T user);
    Collection<T> list (int page, int pageSize);
    T get(Long id);
    T update(T user);
    Boolean delete(Long id);

    User getUserByEmail(String email);
}
