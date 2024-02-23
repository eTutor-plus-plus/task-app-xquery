package at.jku.dke.task_app.xquery.evaluation.execution;

import java.util.regex.Pattern;

/**
 * Executes XQuery queries.
 */
public interface XQProcessor extends AutoCloseable {
    /**
     * The pattern to match forbidden document load functions.
     */
    Pattern forbiddenPattern = Pattern.compile("doc\\((?!['\"]etutor\\.xml['\"]).*\\)|collection\\((?!['\"]etutor\\.xml['\"]).*\\)", Pattern.CASE_INSENSITIVE & Pattern.MULTILINE);

    /**
     * Executes an XQuery query.
     *
     * @param query       The query to evaluate.
     * @param xmlDocument The XML document to use (the method will replace 'etutor.xml' with correct path/db name).
     * @return A String representing the result as it is returned by the underlying XQuery processor.
     * @throws XQueryException If an error occurs during the execution of the query.
     * @see #replaceXmlFileName(String, String)
     */
    String executeQuery(String query, String xmlDocument) throws XQueryException;

    /**
     * Replaces all occurrences of 'etutor.xml' with the given xmlFileName.
     *
     * @param query       The query to evaluate.
     * @param xmlFileName The XML file name to use.
     * @return The query with all occurrences of 'etutor.xml' replaced with the given xmlFileName.
     * @throws InvalidDocumentLoadException If the query contains invalid document load functions.
     */
    static String replaceXmlFileName(String query, String xmlFileName) throws InvalidDocumentLoadException {
        if (forbiddenPattern.matcher(query).find())
            throw new InvalidDocumentLoadException();

        return query.replaceAll("etutor\\.xml", xmlFileName);
    }
}
