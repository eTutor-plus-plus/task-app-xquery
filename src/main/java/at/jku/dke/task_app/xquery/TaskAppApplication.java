package at.jku.dke.task_app.xquery;

import at.jku.dke.etutor.task_app.AppHelper;
import at.jku.dke.task_app.xquery.config.XQuerySettings;
import at.jku.dke.task_app.xquery.evaluation.execution.BaseXProcessor;
import at.jku.dke.task_app.xquery.evaluation.execution.SaxonProcessor;
import at.jku.dke.task_app.xquery.evaluation.execution.XQProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import java.nio.file.Path;

/**
 * The main class of the application.
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@ConfigurationPropertiesScan(basePackageClasses = XQuerySettings.class)
public class TaskAppApplication {

    private static final Logger LOG = LoggerFactory.getLogger(TaskAppApplication.class);

    /**
     * The entry point of the application.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        var app = new SpringApplication(TaskAppApplication.class);
        Environment env = app.run(args).getEnvironment();
        AppHelper.logApplicationStartup(LOG, env);
    }

    /**
     * Creates a new instance of class {@link TaskAppApplication}.
     */
    public TaskAppApplication() {
    }

    /**
     * Provides the XQuery processor.
     *
     * @param settings The XQuery settings.
     * @return The XQuery processor.
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public XQProcessor xqProcessor(XQuerySettings settings) {
        return switch (settings.executor()) {
            case "saxon" -> {
                if (settings.xmlDirectory() == null || settings.xmlDirectory().isBlank())
                    throw new IllegalArgumentException("xmlDirectory must not be null when using saxon processor.");
                yield new SaxonProcessor(Path.of(settings.xmlDirectory()));
            }
            case "basex" -> new BaseXProcessor(settings.xmlDirectory() == null || settings.xmlDirectory().isBlank() ? null : Path.of(settings.xmlDirectory()));
            default -> throw new IllegalStateException("Unexpected executor: " + settings.executor());
        };
    }
}
