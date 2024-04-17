package at.jku.dke.task_app.xquery.evaluation.execution;

import org.basex.core.BaseXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.UUID;

/**
 * Executes XQuery queries using the BaseX API.
 */
public class BaseXProcessor implements XQProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(BaseXProcessor.class);
    private final BaseXApi baseXApi;

    /**
     * Creates a new instance of class {@link BaseXProcessor}.
     *
     * @param basexDirectory The directory where to store the BaseX databases.
     */
    public BaseXProcessor(Path basexDirectory) {
        this.baseXApi = new BaseXApi(basexDirectory);
    }

    /**
     * Executes an XQuery query.
     *
     * @param query       The query to evaluate.
     * @param xmlDocument The XML document to use (the method will replace 'etutor.xml' with correct db name).
     * @return A String representing the result as it is returned by the BaseX XQuery processor.
     * @throws XQueryException If an error occurs during the execution of the query.
     */
    @Override
    public String executeQuery(String query, String xmlDocument) throws XQueryException {
        // Prepare database
        String dbName = "etutor" + UUID.randomUUID().toString().replace("-", "");
        try {
            this.baseXApi.createDatabase(dbName, xmlDocument);
        } catch (BaseXException ex) {
            LOG.error("Could not create database.", ex);
            throw new XQueryException("Could not initialize database.", ex);
        }

        // Prepare query
        query = XQProcessor.replaceXmlFileName(query, dbName);  // call this method only for checking if invalid document loaded in query

        // Execute query
        try {
            LOG.info("Executing query: {}", query);
            return this.baseXApi.executeQuery(query);
        } catch (BaseXException ex) {
            LOG.warn("Could not execute query.", ex);
            throw new XQueryException(ex);
        } finally {
            try {
                this.baseXApi.dropDatabase(dbName);
            } catch (BaseXException ex) {
                LOG.error("Could not drop database.", ex);
            }
        }
    }

    @Override
    public String getVersion() throws XQueryException {
        try {
            var tmp = this.baseXApi.getInfo();
            var versionIdx = tmp.indexOf("Version:");
            var version = tmp.substring(versionIdx + 9, tmp.indexOf("\n", versionIdx));
            return "BaseX " + version;
        } catch (BaseXException ex) {
            throw new XQueryException(ex);
        }
    }

    @Override
    public void close() {
        this.baseXApi.close();
    }
}
