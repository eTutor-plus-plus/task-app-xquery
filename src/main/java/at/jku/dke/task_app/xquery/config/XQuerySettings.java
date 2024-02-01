package at.jku.dke.task_app.xquery.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration settings for the XQuery module.
 *
 * @param xmlFilesDirectory The directory where the XML files are stored.
 */
@Validated
@ConfigurationProperties(prefix = "xquery")
public record XQuerySettings(String xmlFilesDirectory) {
}
