package com.app.ares.enumeration;

import lombok.Getter;

@Getter
public enum EventType {

    LOGIN_ATTEMPT("Login atempted"),
    LOGIN_ATTEMPT_FAILURE("Login failed"),
    LOGIN_ATTEMPT_SUCCESS("Login Successfull"),
    PROFILE_UPDATE("Profile information updated"),
    PROFILE_PICTURE_UPDATE("Profile picture updated"),
    ROLE_UPDATE("User role was updated"),
    ACCOUNT_SETTINGS_UPDATE("Account settings were updated"),
    PASSWORD_UPDATE("User password was updated"),
    MFA_UPDATE("MFA settings were changed");

    private final String desciption;

    EventType(String desciption) {
        this.desciption = desciption;
    }


}
