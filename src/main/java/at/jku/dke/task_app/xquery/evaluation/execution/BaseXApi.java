package at.jku.dke.task_app.xquery.evaluation.execution;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.StaticOptions;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.DropDB;
import org.basex.core.cmd.XQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * Wrapper for BaseX API Core classes.
 */
class BaseXApi implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(BaseXApi.class);
    private final Context context;

    /**
     * Creates a new instance of class {@link BaseXApi}.
     */
    public BaseXApi(Path databasePath) {
        LOG.debug("Creating BaseX context with database path: {}", databasePath);
        var options = new StaticOptions(false);
        options.set(StaticOptions.DBPATH, databasePath.normalize().toAbsolutePath().toString());
        this.context = new Context(options);
    }

    /**
     * Returns the context of the BaseX API.
     * <p>
     * Do NOT manually close the context, use the {@link #close()} method instead.
     *
     * @return The context of the BaseX API.
     */
    public Context getContext() {
        return context;
    }

    /**
     * Creates a new database with the given name and the specified document.
     *
     * @param name     The name of the database.
     * @param document The XML-document to be stored in the database.
     * @throws BaseXException If an error occurs during the creation of the database.
     */
    public void createDatabase(String name, String document) throws BaseXException {
        LOG.debug("Creating database {}", name);
        CreateDB cmd = new CreateDB(name, document);
        cmd.execute(this.context);
    }

    /**
     * Drops the database with the given name.
     *
     * @param name The name of the database to drop.
     * @throws BaseXException If an error occurs during the deletion of the database.
     */
    public void dropDatabase(String name) throws BaseXException {
        LOG.debug("Dropping database {}", name);
        DropDB cmd = new DropDB(name);
        cmd.execute(this.context);
    }

    /**
     * Executes the given query and returns the result.
     *
     * @param query The query to execute.
     * @return The result of the query.
     * @throws BaseXException If an error occurs during the execution of the query.
     */
    public String executeQuery(String query) throws BaseXException {
        LOG.debug("Executing query: {}", query);
        XQuery cmd = new XQuery(query);
        return cmd.execute(this.context);
    }

    /**
     * Closes the BaseX context.
     */
    @Override
    public void close() {
        LOG.debug("Closing BaseX context");
        this.context.close();
    }
}
