package com.app.ares.service.implementation;

import com.app.ares.domain.Role;
import com.app.ares.repository.RoleRepository;
import com.app.ares.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository<Role> roleRepository;

    @Override
    public Role getRoleByUserId(Long id) {
        return roleRepository.getRoleByUserId(id);
    }

    @Override
    public Collection<Role> getRoles() {
        return roleRepository.list();
    }
}
