package com.app.ares.service;

import com.app.ares.domain.Role;

import java.util.Collection;

public interface RoleService {

    Role getRoleByUserId(Long id);
    Collection<Role> getRoles();
}
