package com.example.demo;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class MyHealthIndicator implements HealthIndicator {

    private final ApplicationEventPublisher eventPublisher;

    public MyHealthIndicator(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }


    @Override
    public Health health() {
        int errorCode = check();
        AvailabilityChangeEvent.publish(this.eventPublisher, new NullPointerException("foo"), LivenessState.BROKEN);
        AvailabilityChangeEvent.publish(this.eventPublisher, new NullPointerException("foo"), LivenessState.CORRECT);

        if (errorCode != 0) {
            return Health.down().withDetail("Error Code", errorCode).build();
        }
        return Health.up().build();
    }

    private int check() {
        // perform some specific health check
        return 0;
    }

}
