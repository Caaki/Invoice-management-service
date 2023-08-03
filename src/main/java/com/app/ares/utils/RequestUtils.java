package com.app.ares.utils;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtils {

    public static String getIpAddress(HttpServletRequest request){
        String ipAddress = "Unknown IP";
        if (request != null){
            ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress==null || "".equals(ipAddress)){
                ipAddress = request.getRemoteAddr();
            }
        }
        return ipAddress;
    }



}
