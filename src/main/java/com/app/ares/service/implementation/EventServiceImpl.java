package com.app.ares.service.implementation;

import com.app.ares.domain.UserEvent;
import com.app.ares.enumeration.EventType;
import com.app.ares.repository.EventRepository;
import com.app.ares.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    @Override
    public Collection<UserEvent> getEventByUserId(Long userId) {
        return eventRepository.getEventByUserId(userId);
    }

    @Override
    public void addUserEvent(String email, EventType eventType, String device, String ipAddress) {
        eventRepository.addUserEvent(email,eventType,device,ipAddress);
    }

    @Override
    public void addUserEvent(Long userId, EventType eventType, String device, String ipAddress) {

    }
}
