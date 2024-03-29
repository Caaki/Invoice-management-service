package com.app.ares.event;

import com.app.ares.enumeration.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Getter
@Setter
public class NewUserEvent extends ApplicationEvent {

    private EventType type;
    private String email;

    public NewUserEvent( String email, EventType type) {
        super(email);
        this.email = email;
        this.type = type;
    }
}
