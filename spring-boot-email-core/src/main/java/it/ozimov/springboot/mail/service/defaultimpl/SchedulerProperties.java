package it.ozimov.springboot.mail.service.defaultimpl;

import com.google.common.base.Preconditions;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static it.ozimov.springboot.mail.configuration.ApplicationPropertiesConstants.__SPRING_MAIL_SCHEDULER;
import static java.util.Objects.isNull;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = __SPRING_MAIL_SCHEDULER)
public class SchedulerProperties {

    @Getter(AccessLevel.NONE)
    private boolean enabled;

    // spring.mail.scheduler.priorityLevels
    private Integer priorityLevels = 10;

    // spring.mail.scheduler.persistence.*
    private Persistence persistence = new Persistence();

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Persistence {

        // spring.mail.scheduler.persistence.desiredBatchSize
        private int desiredBatchSize = 500;

        // spring.mail.scheduler.persistence.minKeptInMemory
        private int minKeptInMemory = 250;

        // spring.mail.scheduler.persistence.maxKeptInMemory
        private int maxKeptInMemory = 2000;

    }

    @PostConstruct
    protected boolean validate() {
        if (enabled) {
            checkIsValid(this);
        } else {
            setValuesToNull();
        }
        return true;
    }

    public static void checkIsValid(@NonNull final SchedulerProperties schedulerProperties) {
        Preconditions.checkState(schedulerProperties.getPriorityLevels() > 0,
                "Expected at least one priority level. Review property 'spring.mail.scheduler.priorityLevels'.");

        Preconditions.checkState(isNull(schedulerProperties.getPersistence()) || schedulerProperties.getPersistence().getDesiredBatchSize() > 0,
                "Expected at least a batch of size one, otherwise the persistence layer will not work. Review property 'spring.mail.scheduler.persistence.desiredBatchSize'.");

        Preconditions.checkState(isNull(schedulerProperties.getPersistence()) || schedulerProperties.getPersistence().getMinKeptInMemory() >= 0,
                "Expected a non negative amount of email to be kept in memory. Review property 'spring.mail.scheduler.persistence.minKeptInMemory'.");

        Preconditions.checkState(isNull(schedulerProperties.getPersistence()) || schedulerProperties.getPersistence().getMaxKeptInMemory() > 0,
                "Expected at least one email to be available in memory, otherwise the persistence layer will not work. Review property 'spring.mail.scheduler.persistence.maxKeptInMemory'.");

        Preconditions.checkState(isNull(schedulerProperties.getPersistence()) ||
                        (schedulerProperties.getPersistence().getMaxKeptInMemory() >= schedulerProperties.getPersistence().getMinKeptInMemory()),
                "The application properties key '%s' should not have a value smaller than the value in property '%s'.",
                "spring.mail.scheduler.persistence.maxKeptInMemory", "spring.mail.scheduler.persistence.minKeptInMemory");

        Preconditions.checkState(isNull(schedulerProperties.getPersistence()) ||
                        (schedulerProperties.getPersistence().getMaxKeptInMemory() >= schedulerProperties.getPersistence().getDesiredBatchSize()),
                "The application properties key '%s' should not have a value smaller than the value in property '%s'.",
                "spring.mail.scheduler.persistence.maxKeptInMemory", "spring.mail.scheduler.persistence.desiredBatchSize");
    }

    private void setValuesToNull() {
        priorityLevels = null;
        persistence = null;
    }

}