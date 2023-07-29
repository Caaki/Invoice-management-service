package com.app.ares.query;

public class RoleQuery {

    public static final String INSERT_ROLE_TO_USER_QUERY =
            "INSERT INTO UserRoles " +
            "(user_id, role_id) " +
            "VALUES (:userId, :roleId)";
    public static final String SLECT_ROLE_BY_NAME_QUERY =
            "SELECT * FROM Roles " +
            "WHERE name = :name";

}
