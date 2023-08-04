package com.app.ares.repository.Implementation;

import com.app.ares.domain.UserEvent;
import com.app.ares.enumeration.EventType;
import com.app.ares.repository.EventRepository;
import com.app.ares.rowmapper.UserEventRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;

import static com.app.ares.query.EventQuery.*;


@Repository
@RequiredArgsConstructor
@Slf4j
public class EventRepositoryImpl implements EventRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;


    @Override
    public Collection<UserEvent> getEventByUserId(Long userId) {
        return jdbcTemplate.query(SELECT_EVENTS_BY_USER_ID_QUERY, Map.of(
                "userId", userId), new UserEventRowMapper());

    }

    @Override
    public void addUserEvent(String email, EventType eventType, String device, String ipAddress) {
        jdbcTemplate.update(INSERT_EVENT_BY_USER_EMAIL_QUERY, Map.of(
                "email", email,
                "type", eventType.toString(),
                "device", device,
                "ipAddress", ipAddress));
    }

    @Override
    public void addUserEvent(Long userId, EventType eventType, String device, String ipAddress) {

    }
}
