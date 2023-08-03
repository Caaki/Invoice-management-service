package com.app.ares.service;

import com.app.ares.domain.UserEvent;
import com.app.ares.enumeration.EventType;

import java.util.Collection;

public interface EventService {
    Collection<UserEvent> getEventByUserId(Long userId);

    void addUserEvent(String email, EventType eventType, String device, String ipAddress);

    void addUserEvent(Long userId, EventType eventType, String device, String ipAddress);

}
