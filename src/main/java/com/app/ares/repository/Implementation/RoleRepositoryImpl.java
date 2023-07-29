package com.app.ares.repository.Implementation;

import com.app.ares.domain.Role;
import com.app.ares.exception.ApiException;
import com.app.ares.repository.RoleRepository;
import com.app.ares.rowmapper.RoleRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static com.app.ares.enumeration.RoleType.ROLE_USER;
import static com.app.ares.query.RoleQuery.*;
import static java.util.Objects.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RoleRepositoryImpl implements RoleRepository<Role> {


    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Role create(Role user) {
        return null;
    }

    @Override
    public Collection<Role> list(int page, int pageSize) {
        return null;
    }

    @Override
    public Role get(Long id) {
        return null;
    }

    @Override
    public Role update(Role user) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    @Override
    public void addRoleToUser(Long userid, String roleName) {

        log.info("Adding role {} to user id: {}", roleName, userid);

        try{
            Role role = jdbcTemplate.queryForObject(
                    SLECT_ROLE_BY_NAME_QUERY,
                    Map.of("name", roleName),
                    new RoleRowMapper());
            jdbcTemplate.update(
                    INSERT_ROLE_TO_USER_QUERY,
                    Map.of("userId", userid,"roleId", requireNonNull(role).getId()));


        }catch (EmptyResultDataAccessException e){
            throw new ApiException("No role found by name: " + ROLE_USER.name());
        }catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("An error occurred. Please try again");

        }
    }

    @Override
    public Role getRoleByUserId(Long userId) {
        return null;
    }

    @Override
    public Role getRoleByUserEmail(String email) {
        return null;
    }

    @Override
    public void updateUserRole(Long userId, String roleName) {

    }
}
