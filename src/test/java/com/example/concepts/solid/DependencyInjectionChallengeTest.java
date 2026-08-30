package com.example.concepts.solid;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DependencyInjectionChallengeTest {

    private DependencyInjectionChallenge.DiContainer container;

    @BeforeEach
    void setUp() {
        container = new DependencyInjectionChallenge.DiContainer();
    }

    @Test
    void testSuccessfulDependencyWiring() throws Exception {
        container.register(DependencyInjectionChallenge.OrderService.class);
        container.register(DependencyInjectionChallenge.PaymentService.class);
        container.register(DependencyInjectionChallenge.DatabaseConnection.class);

        container.instantiateAndInject();

        DependencyInjectionChallenge.OrderService orderService =
                container.getBean(DependencyInjectionChallenge.OrderService.class);
        assertThat(orderService).isNotNull();

        String result = orderService.placeOrder();
        assertThat(result)
                .isEqualTo("Order Placed. Payment processed. DB query: Database Query Result");
    }

    @Test
    void testRegisterNonComponentThrowsException() {
        container.register(DependencyInjectionChallenge.UnmanagedClass.class);

        assertThatThrownBy(() -> container.instantiateAndInject())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Class must be annotated with @Component");
    }

    @Test
    void testMissingDependencyThrowsException() {
        container.register(DependencyInjectionChallenge.OrderService.class);
        // Missing PaymentService

        assertThatThrownBy(() -> container.instantiateAndInject())
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("No dependency found of type");
    }
}
