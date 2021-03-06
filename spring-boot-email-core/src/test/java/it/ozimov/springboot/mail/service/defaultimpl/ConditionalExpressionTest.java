package it.ozimov.springboot.mail.service.defaultimpl;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static it.ozimov.springboot.mail.service.defaultimpl.ConditionalExpression.*;
import static org.hamcrest.Matchers.*;

public class ConditionalExpressionTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final JUnitSoftAssertions assertions = new JUnitSoftAssertions();

    @Test
    public void shouldBeUtilityClass() throws Exception {
        //Arrange
        Constructor<?> constructor = ConditionalExpression.class.getDeclaredConstructor();
        assertions.assertThat(Modifier.isPrivate(constructor.getModifiers()))
                .as("Constructor of an Utility Class should be private")
                .isTrue();
        constructor.setAccessible(true);

        expectedException.expectCause(
                allOf(instanceOf(UnsupportedOperationException.class),
                        hasProperty("message", equalTo("This is a utility class and cannot be instantiated"))
                ));

        //Act
        constructor.newInstance();
    }

    @Test
    public void shouldConstantsRemainUnchanged() {
        assertions.assertThat(SCHEDULER_IS_ENABLED)
                .as("The condition for enabling the scheduler should not change")
                .isEqualTo("'${spring.mail.scheduler.enabled:false}' == 'true'");

        assertions.assertThat(PERSISTENCE_IS_ENABLED)
                .as("The condition for enabling the persistence layer should not change")
                .isEqualTo("'${spring.mail.scheduler.enabled:false}' == 'true'" +
                        " && '${spring.mail.scheduler.persistence.enabled:false}' == 'true'");

        assertions.assertThat(PERSISTENCE_IS_ENABLED_WITH_REDIS)
                .as("The condition for enabling the persistence layer using redis should not change")
                .isEqualTo("'${spring.mail.scheduler.enabled:false}' == 'true'" +
                        " && '${spring.mail.scheduler.persistence.enabled:false}' == 'true'" +
                        " && '${spring.mail.scheduler.persistence.redis.enabled:false}' == 'true'");

        assertions.assertThat(PERSISTENCE_IS_ENABLED_WITH_EMBEDDED_REDIS)
                .as("The condition for enabling the persistence layer using embedded redis should not change")
                .isEqualTo("'${spring.mail.scheduler.enabled:false}' == 'true'" +
                        " && '${spring.mail.scheduler.persistence.enabled:false}' == 'true'" +
                        " && '${spring.mail.scheduler.persistence.redis.enabled:false}' == 'true'" +
                        " && '${spring.mail.scheduler.persistence.redis.embedded:false}' == 'true'");
    }

}