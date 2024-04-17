package at.jku.dke.task_app.xquery.evaluation.execution;

import net.sf.saxon.s9api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Executes XQuery queries using the Saxon API.
 */
public class SaxonProcessor implements XQProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(SaxonProcessor.class);
    private final XQueryCompiler compiler;
    private final Path basePath;

    /**
     * Creates a new instance of class {@link SaxonProcessor}.
     *
     * @param tempPath The path where to store temporary files.
     */
    public SaxonProcessor(Path tempPath) {
        if (tempPath == null)
            throw new IllegalArgumentException("tempPath must not be null.");

        this.basePath = tempPath.normalize().toAbsolutePath();
        this.compiler = new Processor(false).newXQueryCompiler();
        this.compiler.setBaseURI(this.basePath.toUri());

        if (!this.basePath.toFile().exists())
            this.basePath.toFile().mkdirs();

        LOG.debug("XQuery Version: {}", this.compiler.getLanguageVersion());
        LOG.debug("Base Path: {}", this.basePath);
    }

    /**
     * Executes the given query on the given XML document.
     *
     * @param query       The query to evaluate.
     * @param xmlDocument ThThe XML document to use (the method will replace 'etutor.xml' with correct path).
     * @return A String representing the result as it is returned by the Saxon XQuery processor.
     * @throws XQueryException If an error occurs during query execution.
     */
    @Override
    public String executeQuery(String query, String xmlDocument) throws XQueryException {
        // Create file
        Path xmlPath = this.basePath.resolve(UUID.randomUUID() + ".xml");
        LOG.debug("Creating temporary file for XML document: {}", xmlPath);
        try {
            Files.writeString(xmlPath, xmlDocument);
        } catch (IOException ex) {
            LOG.error("Error creating temporary file for XML document.", ex);
            throw new XQueryException("Could not load XML document.", ex);
        }

        // Prepare query
        query = XQProcessor.replaceXmlFileName(query, this.basePath.relativize(xmlPath).toString());

        // Execute query
        try {
            LOG.info("Executing query {} on document: {}", query, xmlPath);
            XQueryExecutable executable = this.compiler.compile(query);
            XQueryEvaluator evaluator = executable.load();
            XdmValue result = evaluator.evaluate(); // internally ensures that the query is not an updating query
            return result.toString();
        } catch (SaxonApiException ex) {
            LOG.warn("Error during query execution.", ex);
            throw new XQueryException(ex);
        } finally {
            // Clean up
            try {
                Files.deleteIfExists(xmlPath);
            } catch (IOException ex) {
                LOG.debug("Error deleting temporary file for XML document.", ex);
            }
        }
    }

    @Override
    public String getVersion() {
        return "Saxon " + this.compiler.getProcessor().getSaxonProductVersion()
               + " " + this.compiler.getProcessor().getSaxonEdition();
    }

    @Override
    public void close() {
        // Nothing to do
    }
}
