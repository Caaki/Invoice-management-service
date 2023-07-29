package com.app.ares.repository;

import com.app.ares.domain.Role;

import java.util.Collection;

public interface RoleRepository <T extends Role>{

    T create(T user);
    Collection<T> list (int page, int pageSize);
    T get(Long id);
    T update(T user);
    Boolean delete(Long id);

    void addRoleToUser(Long userid, String roleName);

    Role getRoleByUserId(Long userId);

    Role getRoleByUserEmail(String email);

    void updateUserRole(Long userId, String roleName);
}
