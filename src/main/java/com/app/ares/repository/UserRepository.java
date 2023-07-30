package com.app.ares.repository;

import com.app.ares.domain.User;
import com.app.ares.dto.UserDTO;

import java.util.Collection;

public interface UserRepository <T extends User>{

    T create(T user);
    Collection<T> list (int page, int pageSize);
    T get(Long id);
    T update(T user);
    Boolean delete(Long id);

    User getUserByEmail(String email);

    void sendVerificationCode(UserDTO user);

    User verifyCode(String email, String code);
}
