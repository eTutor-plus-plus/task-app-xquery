package at.jku.dke.task_app.xquery.evaluation.execution;

/**
 * Thrown to indicate that a not allowed document was attempted to be loaded, i.e. document other than etutor.xml loaded.
 */
public class InvalidDocumentLoadException extends XQueryException {
    /**
     * Creates a new instance of class {@link InvalidDocumentLoadException}.
     */
    public InvalidDocumentLoadException() {
    }
}
