package com.example.concepts.solid;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class ValidationFrameworkChallengeTest {

    @Test
    void testValidUserValidation() throws IllegalAccessException {
        ValidationFrameworkChallenge.User user =
                new ValidationFrameworkChallenge.User("JohnDoe", 20, "john@example.com");

        List<String> errors = ValidationFrameworkChallenge.validate(user);
        assertThat(errors).isEmpty();
    }

    @Test
    void testInvalidUserValidation() throws IllegalAccessException {
        ValidationFrameworkChallenge.User user =
                new ValidationFrameworkChallenge.User("  ", 15, "invalid-email");

        List<String> errors = ValidationFrameworkChallenge.validate(user);

        assertThat(errors).hasSize(3);
        assertThat(errors).anyMatch(e -> e.contains("username: Username cannot be empty"));
        assertThat(errors).anyMatch(e -> e.contains("age: Age must be at least 18"));
        assertThat(errors).anyMatch(e -> e.contains("email: Email must be valid"));
    }

    @Test
    void testUnvalidatedClassThrowsException() {
        ValidationFrameworkChallenge.UnvalidatedClass unvalidated =
                new ValidationFrameworkChallenge.UnvalidatedClass();

        assertThatThrownBy(() -> ValidationFrameworkChallenge.validate(unvalidated))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Class must be annotated with @Validate");
    }
}
