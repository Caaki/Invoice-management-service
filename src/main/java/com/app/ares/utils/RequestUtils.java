package com.app.ares.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

import static nl.basjes.parse.useragent.UserAgent.*;

@Slf4j
public class RequestUtils {

    public static final String USER_AGENT_HEAD = "user-agent";
    public static final String X_FORWARDED_FOR_HEADER = "X-FORWARDED-FOR";

    public static String getIpAddress(HttpServletRequest request){
        String ipAddress = "Unknown IP";
        if (request != null){
            ipAddress = request.getHeader(X_FORWARDED_FOR_HEADER);
            if (ipAddress==null || "".equals(ipAddress)){
                ipAddress = request.getRemoteAddr();
            }
        }
        return ipAddress;
    }

    public static String getDevice(HttpServletRequest request){
        UserAgentAnalyzer userAgentAnalyzer = UserAgentAnalyzer
                .newBuilder()
                .hideMatcherLoadStats()
                .withCache(1000)
                .build();
        UserAgent agent = userAgentAnalyzer.parse(request.getHeader(USER_AGENT_HEAD));
        return agent.getValue(AGENT_NAME) + " - " + agent.getValue(DEVICE_NAME) + " [" + agent.getValue(OPERATING_SYSTEM_NAME)+"]";
    }

}
