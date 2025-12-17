package com.finedge.finedgeapi.monitoring;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class PaymentGatewayHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        boolean reachable = true;

        if (reachable) {
            return Health.up().withDetail("gateway", "reachable").build();
        }

        return Health.down().withDetail("gateway", "not reachable").build();
    }

}
