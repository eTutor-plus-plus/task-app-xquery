package at.jku.dke.task_app.xquery.evaluation.execution;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * This class is a health indicator providing the XQuery executor version.
 */
@Component
public class XqHealthIndicator implements HealthIndicator {

    private final XQProcessor executor;
    private String version;

    public XqHealthIndicator(XQProcessor executor) {
        this.executor = executor;
    }

    /**
     * Return an indication of health.
     *
     * @return the health
     */
    @Override
    public Health health() {
        try {
            this.version = this.version == null ? this.executor.getVersion() : this.version;
            return Health.up()
                .withDetail("version", version)
                .build();
        } catch (Exception ex) {
            return Health.down()
                .withException(ex)
                .build();
        }
    }
}
