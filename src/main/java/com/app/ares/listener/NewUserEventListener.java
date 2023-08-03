package com.app.ares.listener;

import com.app.ares.event.NewUserEvent;
import com.app.ares.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewUserEventListener {

    private final EventService eventService;
    private final HttpServletRequest request;

    @EventListener
    public void onNewUserEvent(NewUserEvent  event){
        log.info("NewUserEvent() is called");
        eventService.addUserEvent(event.getEmail(),event.getType(),"Device","Ip address");
    }



}