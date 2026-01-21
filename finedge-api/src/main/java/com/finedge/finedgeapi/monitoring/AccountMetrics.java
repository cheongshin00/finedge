package com.finedge.finedgeapi.monitoring;

import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class AccountMetrics {

    private final Counter accountCreateCounter;

    public AccountMetrics(MeterRegistry registry) {
        this.accountCreateCounter = Counter.builder("account.created")
                .description("Number of accounts created")
                .register(registry);
    }

    public void increment() {
        accountCreateCounter.increment();
    }
}
