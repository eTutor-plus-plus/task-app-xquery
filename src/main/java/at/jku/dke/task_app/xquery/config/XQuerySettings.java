package at.jku.dke.task_app.xquery.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration settings for the XQuery module.
 *
 * @param executor     The executor to use for the XQuery processing (either "basex" or "saxon").
 * @param xmlDirectory The directory where the temporary XML files (saxon) or the BaseX database is stored.
 */
@Validated
@ConfigurationProperties(prefix = "xquery")
public record XQuerySettings(@NotNull String executor, String xmlDirectory) {
}
