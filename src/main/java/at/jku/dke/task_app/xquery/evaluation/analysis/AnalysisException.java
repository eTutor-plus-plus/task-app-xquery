package at.jku.dke.task_app.xquery.evaluation.analysis;

/**
 * Exception thrown when an error occurs during the analysis of an XQuery result.
 */
public class AnalysisException extends Exception {

    /**
     * Creates a new instance of class {@link AnalysisException}.
     *
     * @param message The message of the exception.
     */
    public AnalysisException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of class {@link AnalysisException}.
     *
     * @param message The message of the exception.
     * @param cause   The cause of the exception.
     */
    public AnalysisException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance of class {@link AnalysisException}.
     *
     * @param cause The cause of the exception.
     */
    public AnalysisException(Throwable cause) {
        super(cause);
    }
    
}
