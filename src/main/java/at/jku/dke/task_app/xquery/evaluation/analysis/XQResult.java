package at.jku.dke.task_app.xquery.evaluation.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Represents the result of an XQuery execution.
 */
public class XQResult {
    /**
     * Used as the name of the XML root of an XQuery result. Results are interpreted as XML
     * fragments, which have to be embedded into a root element of an XML document in order to make
     * it analyzable and comparable with another result. The root element should never be part of
     * the analysis result.
     */
    public static final String XML_ROOT = "xquery-result";
    private static final Logger LOG = LoggerFactory.getLogger(XQResult.class);

    private final String rawResult;
    private Document resultDocument;
    private String resultDocumentRaw;
    private SAXException parseException;
    private Path dtdFile;
    private Path resultFile;

    /**
     * Creates a new instance of class {@link XQResult}.
     *
     * @param rawResult The raw result of the XQuery execution.
     * @throws AnalysisException If the raw result could not be parsed into an XML document.
     */
    public XQResult(String rawResult) throws AnalysisException {
        Objects.requireNonNull(rawResult);
        this.rawResult = rawResult;
        this.parseRawResult();
    }

    //#region --- Simple Getter ---

    /**
     * Returns the raw result of the XQuery execution.
     *
     * @return The raw result of the XQuery execution.
     */
    public String getRawResult() {
        return rawResult;
    }

    /**
     * Returns the result of the XQuery execution as an XML document.
     *
     * @return The result of the XQuery execution as an XML document or {@code null} if {@link #getParseException()} is not {@code null}.
     */
    public Document getResultDocument() {
        return resultDocument;
    }

    /**
     * Returns the result of the XQuery execution as an XML string.
     *
     * @return The result of the XQuery execution as an XML string or {@code null} if {@link #getParseException()} is not {@code null}.
     */
    public String getResultDocumentRaw() {
        return resultDocumentRaw;
    }

    /**
     * Returns the parse exception that occurred during the parsing of the raw result.
     *
     * @return The parse exception that occurred during the parsing of the raw result or {@code null} if the raw result could be parsed successfully.
     */
    public SAXException getParseException() {
        return parseException;
    }

    //#endregion

    //#region --- Result ---

    /**
     * Returns the path to the result file of the XQuery execution.
     *
     * @return The path to the result file of the XQuery execution.
     * @throws AnalysisException If the result file could not be created.
     */
    public Path getResultFile() throws AnalysisException {
        return this.getResultFile(false);
    }

    /**
     * Returns the path to the result file of the XQuery execution.
     *
     * @param forceRegeneration If {@code true}, the result file is regenerated; otherwise, the cached result file is returned (if available).
     * @return The path to the result file of the XQuery execution.
     * @throws AnalysisException If the result file could not be created.
     */
    public Path getResultFile(boolean forceRegeneration) throws AnalysisException {
        if (this.resultFile == null || forceRegeneration) {
            try {
                this.resultFile = Files.createTempFile("xq-result", ".xml");
                if (this.resultDocument == null) {
                    Files.writeString(this.resultFile, this.rawResult);
                } else {
                    DOMSource domSource = new DOMSource(this.resultDocument);
                    TransformerFactory tf = TransformerFactory.newInstance();
                    Transformer transformer = tf.newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.transform(domSource, new StreamResult(this.resultFile.toFile()));
                }
            } catch (IOException | TransformerException ex) {
                LOG.error("Could not write XML document to file.", ex);
                throw new AnalysisException("A fatal error occurred when creating the query result file.", ex);
            }
        }
        return this.resultFile;
    }

    //#endregion

    //#region --- DTD ---

    /**
     * Returns the path to the DTD file for the result file.
     *
     * @return The path to the DTD file for the result file.
     * @throws AnalysisException If the DTD file could not be created.
     */
    public Path getDTDFile() throws AnalysisException {
        return this.getDTDFile(false);
    }

    /**
     * Returns the path to the DTD file for the result file.
     *
     * @param forceRegeneration If {@code true}, the DTD file is regenerated; otherwise, the cached DTD file is returned (if available).
     * @return The path to the DTD file for the result file.
     * @throws AnalysisException If the DTD file could not be created.
     */
    public Path getDTDFile(boolean forceRegeneration) throws AnalysisException {
        if (this.parseException != null)
            throw new AnalysisException("The raw result could not be parsed into an XML document.", this.parseException);

        if (this.dtdFile == null || forceRegeneration) {
            try {
                this.dtdFile = this.generateDTD(this.getResultFile(forceRegeneration));
            } catch (IOException ex) {
                LOG.error("Could not generate DTD file.", ex);
                throw new AnalysisException("A fatal error occurred when generating the document type definition from the query result.", ex);
            }
        }

        return this.dtdFile;
    }

    /**
     * Generates a DTD file for the result file.
     *
     * @param resultFile The result file.
     * @return The path to the DTD file.
     * @throws IOException If an I/O error occurs.
     */
    private Path generateDTD(Path resultFile) throws IOException {
        var gen = new DTDGenerator();
        gen.run(resultFile.toString());
        String dtd = gen.printDTD();

        var file = Files.createTempFile("xq-dtd", ".dtd");
        Files.writeString(file, dtd);

        return file;
    }

    //#endregion

    /**
     * Parses the raw result into an XML document.
     */
    private void parseRawResult() throws AnalysisException {
        try {
            LOG.debug("Parsing XML document");
            this.resultDocumentRaw = '<' + XQResult.XML_ROOT + '>' + System.lineSeparator() + this.rawResult + System.lineSeparator() + "</" + XQResult.XML_ROOT + '>';
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            this.resultDocument = builder.parse(new ByteArrayInputStream(this.resultDocumentRaw.getBytes(StandardCharsets.UTF_8)));
        } catch (ParserConfigurationException ex) {
            LOG.error("Could not create document builder.", ex);
            throw new AnalysisException("A fatal error occurred when creating the XML parser.", ex);
        } catch (IOException ex) {
            LOG.error("Could not convert XML document to byte-stream.", ex);
            throw new AnalysisException("A fatal error occurred when parsing the XML document.", ex);
        } catch (SAXException ex) {
            LOG.warn("Invalid XML document.", ex);
            this.parseException = ex;
            this.resultDocument = null;
        }
    }

    // TODO: validate sortedNodes
}
