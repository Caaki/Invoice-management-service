package com.app.ares.constants;

import com.app.ares.configuration.security.provider.TokenProvider;

import java.time.format.DateTimeFormatter;

public class Constants {
    //Security configuration constants
    public static final String[] PUBLIC_URLS ={
            "/user/login/**",
            "/user/register/**",
            "/user/verify/code/**",
            "/user/verify/password/**",
            "/user/resetpassword/**",
            "/user/verify/account/**",
            "/user/refresh/token/**",
            "/user/image/**",
            "/user/new/password**"

    };


    //Filter constants
    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String HTTP_METHOD_OPTIONS = "OPTIONS";

    public static final String [] PUBLIC_ROUTES = {
            "/user/login",
            "/user/register",
            "/user/verify/code",
            "/user/refresh/token",
            "/user/image",
            "/user/new/password"
    };


    //Token provider constants
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String ARES ="ARES";
    public static final String CUSTOMER_MANAGEMENT_SERVICE = "CUSTOMER_MANAGEMENT_SERVICE";
    public static final String AUTHORITIES = "authorities";
    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 1_800_000;
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 432_000_000;



    //RequestUtils constants
    public static final String USER_AGENT_HEAD = "user-agent";
    public static final String X_FORWARDED_FOR_HEADER = "X-FORWARDED-FOR";


    //Date format
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


}
