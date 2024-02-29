package at.jku.dke.task_app.xquery.evaluation.execution;

/**
 * Exception thrown when an error occurs during the execution of an XQuery query.
 */
public class XQueryException extends Exception {
    /**
     * Creates a new instance of class {@link XQueryException}.
     */
    protected XQueryException() {
    }

    /**
     * Creates a new instance of class {@link XQueryException}.
     *
     * @param message The message of the exception.
     */
    public XQueryException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of class {@link XQueryException}.
     *
     * @param message The message of the exception.
     * @param cause   The cause of the exception.
     */
    public XQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance of class {@link XQueryException}.
     *
     * @param cause The cause of the exception.
     */
    public XQueryException(Throwable cause) {
        super(cause);
    }
}
