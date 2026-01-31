package com.nimisha.invoicesystem;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

class InvoicesystemApplicationMainTests {

    @Test
    void runStartsAndStopsContext() {
        try (ConfigurableApplicationContext context = InvoicesystemApplication.run(
                "--spring.main.web-application-type=none")) {
            assertTrue(context.isActive());
        }
    }
}
