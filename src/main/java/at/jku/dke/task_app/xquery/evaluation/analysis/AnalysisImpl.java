package at.jku.dke.task_app.xquery.evaluation.analysis;

import at.jku.dke.task_app.xquery.evaluation.EvaluationService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import net.sf.saxon.s9api.*;
import org.pageseeder.diffx.DiffException;
import org.pageseeder.diffx.Main;
import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Analyzes and prepares the evaluation of a submission.
 */
public class AnalysisImpl implements Analysis {
    private static final Logger LOG = LoggerFactory.getLogger(EvaluationService.class);

    private final XQResult submissionResult;
    private final XQResult solutionResult;
    private final List<String> sorting;

    private boolean schemaValid;
    private Document diffDocument;
    private List<NodeModel> missingNodes;
    private List<NodeModel> superfluousNodes;
    private List<IncorrectTextValueModel> incorrectTextValues;
    private List<NodeModel> displacedNodes;
    private List<AttributeModel> missingAttributes;
    private List<AttributeModel> superfluousAttributes;
    private List<IncorrectAttributeValueModel> incorrectAttributeValues;

    /**
     * Creates a new instance of class {@link AnalysisImpl} and analyzes the results.
     *
     * @param submissionResult The result of the submission.
     * @param solutionResult   The result of the solution.
     * @param sorting          The XPath expressions to check the sorting of the nodes.
     * @throws NullPointerException If {@code submissionResult} or {@code solutionResult} is {@code null}.
     * @throws AnalysisException    If an error occurs during analysis.
     */
    public AnalysisImpl(XQResult submissionResult, XQResult solutionResult, List<String> sorting) throws AnalysisException {
        Objects.requireNonNull(submissionResult);
        Objects.requireNonNull(solutionResult);

        this.submissionResult = submissionResult;
        this.solutionResult = solutionResult;
        this.schemaValid = false;
        this.sorting = sorting == null ? List.of() : sorting;
        this.validateSortingExpressions();
        this.analyze();
    }

    //#region --- GETTER ---

    /**
     * Gets the submission result.
     *
     * @return The submission result.
     */
    @Override
    public XQResult getSubmissionResult() {
        return submissionResult;
    }

    /**
     * Gets the solution result.
     *
     * @return The solution result.
     */
    @Override
    public XQResult getSolutionResult() {
        return solutionResult;
    }

    /**
     * Returns whether the submission is fully correct.
     *
     * @return {@code true} if the submission is correct; otherwise {@code false}.
     */
    @Override
    public boolean isCorrect() {
        return this.isSchemaValid() &&
               this.missingNodes.isEmpty() &&
               this.superfluousNodes.isEmpty() &&
               this.incorrectTextValues.isEmpty() &&
               this.displacedNodes.isEmpty() &&
               this.missingAttributes.isEmpty() &&
               this.superfluousAttributes.isEmpty() &&
               this.incorrectAttributeValues.isEmpty();
    }

    /**
     * Returns whether the schema is valid, i.e. the schema of the submission result is valid with regard to the solution result.
     *
     * @return {@code true} if the schema is valid; otherwise {@code false}.
     */
    @Override
    public boolean isSchemaValid() {
        return schemaValid;
    }

    /**
     * Returns the diff document.
     *
     * @return The document with diff annotations.
     */
    @Override
    public Document getDiffDocument() {
        return diffDocument;
    }

    /**
     * Returns the missing nodes.
     * <p>
     * Missing nodes are nodes that are contained in the solution result, but not in the submission result.
     *
     * @return The missing nodes.
     */
    @Override
    public List<NodeModel> getMissingNodes() {
        return missingNodes;
    }

    /**
     * Returns the superfluous nodes.
     * <p>
     * Superfluous nodes are nodes that are contained in the submission result, but not in the solution result.
     *
     * @return The superfluous nodes.
     */
    @Override
    public List<NodeModel> getSuperfluousNodes() {
        return superfluousNodes;
    }

    /**
     * Returns the incorrect text values.
     * <p>
     * Incorrect text values are elements that are contained in the submission result, but have a different value than in the solution result.
     *
     * @return The incorrect text values.
     */
    @Override
    public List<IncorrectTextValueModel> getIncorrectTextValues() {
        return incorrectTextValues;
    }

    /**
     * Returns the displaced nodes.
     * <p>
     * Displaced nodes are nodes that are contained in the submission result, but not at the expected position.
     *
     * @return The displaced nodes.
     */
    @Override
    public List<NodeModel> getDisplacedNodes() {
        return displacedNodes;
    }

    /**
     * Returns the missing attributes.
     * <p>
     * Missing attributes are attributes that are contained in the solution result, but not in the submission result.
     *
     * @return The missing attributes.
     */
    @Override
    public List<AttributeModel> getMissingAttributes() {
        return missingAttributes;
    }

    /**
     * Returns the superfluous attributes.
     * <p>
     * Superfluous attributes are attributes that are contained in the submission result, but not in the solution result.
     *
     * @return The superfluous attributes.
     */
    @Override
    public List<AttributeModel> getSuperfluousAttributes() {
        return superfluousAttributes;
    }

    /**
     * Returns the incorrect attribute values.
     * <p>
     * Incorrect attribute values are attributes that are contained in the submission result, but have a different value than in the solution result.
     *
     * @return The incorrect attribute values.
     */
    @Override
    public List<IncorrectAttributeValueModel> getIncorrectAttributeValues() {
        return incorrectAttributeValues;
    }

    //#endregion

    /**
     * Analyzes the results.
     *
     * @throws AnalysisException If an error occurs during analysis.
     */
    private void analyze() throws AnalysisException {
        this.schemaValid = this.analyzeStructure();
        this.compare();
    }

    /**
     * Analyzes the structure of the submission and the solution by comparing the DTD.
     * <p>
     * This can be considered as the first step when analyzing the differences between two
     * solutions. If the submitted solution is not valid with regard to the given DTD, this means
     * that basically the structure of the result is incorrect. So any further information about
     * errors (displaced elements, missing attributes, ...) is understood as hint, how to structure
     * the result of a query, so that in could be correct at all.
     *
     * @return {@code true} if no validation error could be detected, {@code false} if at least one validation error could be detected.
     * @throws RuntimeException If the submission result could not be validated against the solution DTD.
     */
    private boolean analyzeStructure() throws AnalysisException {
        // Convert result doc to string
        String xml = this.submissionResult.getResultDocumentRaw();

        // Read DTD
        String dtd;
        try {
            dtd = Files.readString(this.solutionResult.getDTDFile());
        } catch (IOException ex) {
            LOG.error("Could not read solution DTD.", ex);
            throw new AnalysisException("A fatal error occurred when reading the DTD file.", ex);
        }

        // Build XML again and validate against DTD
        List<SAXParseException> errors = new ArrayList<>();
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setValidating(true);
            var builder = domFactory.newDocumentBuilder();
            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) {
                    LOG.debug("DTD Validation Warning: {}", exception.toString());
                    errors.add(exception);
                }

                @Override
                public void error(SAXParseException exception) {
                    LOG.debug("DTD Validation Error: {}", exception.toString());
                    errors.add(exception);
                }

                @Override
                public void fatalError(SAXParseException exception) {
                    LOG.debug("DTD Validation Fatal Error: {}", exception.toString());
                    errors.add(exception);
                }
            });
            String tmp = "<!DOCTYPE " + XQResult.XML_ROOT + " [" + System.lineSeparator() + dtd + System.lineSeparator() + "]>" + System.lineSeparator() + xml;
            builder.parse(new ByteArrayInputStream(tmp.getBytes(StandardCharsets.UTF_8)));
            return errors.isEmpty();
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            LOG.error("Could not validate submission result against solution DTD.", ex);
            throw new AnalysisException("A fatal error occurred when parsing the XML document against a DTD.", ex);
        }
    }

    /**
     * Compares the submission and the solution.
     *
     * @throws AnalysisException If an error occurs during comparison.
     */
    private void compare() throws AnalysisException {
        if (this.solutionResult.getResultDocument() == null || this.submissionResult.getResultDocument() == null)
            return; // cannot compare if not both are valid XML documents

        String diff = this.generateDiff();
        this.diffDocument = this.transformDiffToAnalysis(diff);
        Element root = this.diffDocument.getDocumentElement();

        // missingNodes
        this.missingNodes = new ArrayList<>();
        loadDiffNodes(root, "missingNodes", this.missingNodes::add, e -> xmlToObject(e, NodeModel.class));

        // missingNodes
        this.superfluousNodes = new ArrayList<>();
        loadDiffNodes(root, "superfluousNodes", this.superfluousNodes::add, e -> xmlToObject(e, NodeModel.class));

        // missingAttributes
        this.missingAttributes = new ArrayList<>();
        loadDiffNodes(root, "missingAttributes", this.missingAttributes::add, e -> xmlToObject(e, AttributeModel.class));

        // superfluousAttributes
        this.superfluousAttributes = new ArrayList<>();
        loadDiffNodes(root, "superfluousAttributes", this.superfluousAttributes::add, e -> xmlToObject(e, AttributeModel.class));

        // incorrectAttributeValues
        this.incorrectAttributeValues = new ArrayList<>();
        loadDiffNodes(root, "incorrectAttributeValues", this.incorrectAttributeValues::add, e -> xmlToObject(e, IncorrectAttributeValueModel.class));

        // incorrectTextValues
        this.incorrectTextValues = new ArrayList<>();
        loadDiffNodes(root, "incorrectTextValues", this.incorrectTextValues::add, e -> xmlToObject(e, IncorrectTextValueModel.class));

        // displacedNodes
        this.displacedNodes = new ArrayList<>();
        this.checkSorting();
    }

    /**
     * Loads the difference nodes from the diff XML.
     *
     * @param root       The root element of the diff XML.
     * @param tagName    The tag name of the difference nodes.
     * @param addElement The consumer to add the difference nodes.
     */
    private static <T> void loadDiffNodes(Element root, String tagName, Consumer<T> addElement, Function<Element, T> mapper) {
        NodeList nodeList = root.getElementsByTagName(tagName);
        if (nodeList.getLength() <= 0)
            return;

        Element element = (Element) nodeList.item(0);
        nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
                addElement.accept(mapper.apply((Element) node));
        }
    }

    /**
     * Unmarshals the XML to an object.
     *
     * @param element The XML element.
     * @param clazz   The class of the object.
     * @param <T>     The type of the object.
     * @return The object.
     */
    private static <T> T xmlToObject(Element element, Class<T> clazz) {
        try {
            JAXBContext ctx = JAXBContext.newInstance(clazz);
            return ctx.createUnmarshaller().unmarshal(element, clazz).getValue();
        } catch (JAXBException ex) {
            LOG.error("Could not unmarshal XML to object.", ex);
            throw new RuntimeException("A fatal error occurred when parsing diff result.", ex);
        }
    }

    /**
     * Generates the diff between the submission and the solution.
     *
     * @return The diff as XML.
     * @throws AnalysisException If the diff could not be generated.
     */
    private String generateDiff() throws AnalysisException {
        try (var submissionReader = new StringReader(this.submissionResult.getResultDocumentRaw());
             var solutionReader = new StringReader(this.solutionResult.getResultDocumentRaw());
             var writer = new StringWriter()) {
            Main.diff(submissionReader, solutionReader, writer, new DiffConfig(false, WhiteSpaceProcessing.IGNORE, TextGranularity.TEXT));

            return writer.toString();
        } catch (IOException | DiffException ex) {
            LOG.error("Could not generate diff.", ex);
            throw new AnalysisException("Could not calculate the difference of submission and solution documents.", ex);
        }
    }

    /**
     * Transforms the diff XML to an analysis XML.
     *
     * @param diffXml The diff XML.
     * @return The analysis XML.
     * @throws AnalysisException If the diff XML could not be transformed.
     */
    private Document transformDiffToAnalysis(String diffXml) throws AnalysisException {
        try (var writer = new StringWriter();
             var reader = new StringReader(diffXml);
             var xslt = this.getClass().getClassLoader().getResourceAsStream("transform.xslt")) {
            Processor processor = new Processor(false);
            XsltCompiler compiler = processor.newXsltCompiler();

            XsltExecutable stylesheet = compiler.compile(new StreamSource(xslt));

            Serializer out = processor.newSerializer(writer);
            out.setOutputProperty(Serializer.Property.METHOD, "xml");
            out.setOutputProperty(Serializer.Property.INDENT, "yes");
            out.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");

            Xslt30Transformer transformer = stylesheet.load30();
            transformer.transform(new StreamSource(reader), out);

            String result = writer.toString();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException | SaxonApiException ex) {
            LOG.error("Could not transform diff XML document.", ex);
            throw new AnalysisException("A fatal error occurred when transforming the diff XML.", ex);
        } catch (ParserConfigurationException ex) {
            LOG.error("Could not parse diff XML document.", ex);
            throw new AnalysisException("A fatal error occurred when creating the XML parser.", ex);
        } catch (SAXException ex) {
            LOG.error("Could not parse diff XML document.", ex);
            throw new AnalysisException("A fatal error occurred when parsing the diff XML document.", ex);
        }
    }

    /**
     * Checks the sorting of the result elements.
     *
     * @throws AnalysisException If an error occurs during sorting check.
     */
    private void checkSorting() throws AnalysisException {
        assert this.getSubmissionResult().getResultDocument() != null;
        if (this.sorting.isEmpty())
            return;

        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            for (String expression : this.sorting) {
                XPathExpression xExpr = xpath.compile(expression);
                NodeList submissionList = (NodeList) xExpr.evaluate(this.getSubmissionResult().getResultDocument(), XPathConstants.NODESET);
                NodeList solutionList = (NodeList) xExpr.evaluate(this.getSolutionResult().getResultDocument(), XPathConstants.NODESET);

                if (submissionList.getLength() != solutionList.getLength())
                    continue; // do not check, something else is wrong, this line should never be reached

                for (int i = 0; i < submissionList.getLength(); i++) {
                    Node submissionNode = submissionList.item(i);
                    Node solutionNode = solutionList.item(i);

                    if (!submissionNode.isEqualNode(solutionNode))
                        this.displacedNodes.add(new NodeModel(expression, submissionNode.getNodeName()));
                }
            }
        } catch (XPathExpressionException ex) {
            LOG.error("Could not compile XPath expression.", ex);
            throw new AnalysisException("A fatal error occurred while analyzing the order of the result elements.", ex);
        }
    }

    /**
     * Validates the sorting XPath expressions.
     *
     * @throws AnalysisException If an expression is invalid.
     */
    private void validateSortingExpressions() throws AnalysisException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        for (String expression : this.sorting) {
            try {
                XPathExpression xExpr = xpath.compile(expression);
                NodeList solutionList = (NodeList) xExpr.evaluate(this.getSolutionResult().getResultDocument(), XPathConstants.NODESET);

                if (solutionList.getLength() == 0)
                    throw new AnalysisException("The sorting expression '" + expression + "' does not match any nodes in the solution result.");
            } catch (XPathExpressionException ex) {
                LOG.error("Could not compile XPath expression: " + expression, ex);
                throw new AnalysisException("A fatal error occurred while compiling XPath expression: " + expression, ex);
            }
        }
    }
}
