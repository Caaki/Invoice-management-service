package com.app.ares.utils;

import com.app.ares.domain.UserPrincipal;
import com.app.ares.dto.UserDTO;
import org.springframework.security.core.Authentication;

public class UserUtils {

    public static UserDTO getAuthenticatedUser(Authentication authentication){
        return ((UserDTO) authentication.getPrincipal());
    }

    public static UserDTO getLoggedInUser(Authentication authentication){
        return ((UserPrincipal) authentication.getPrincipal()).getUser();
    }
}
