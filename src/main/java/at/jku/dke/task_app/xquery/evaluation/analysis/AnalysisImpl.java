package at.jku.dke.task_app.xquery.evaluation.analysis;

import at.jku.dke.task_app.xquery.data.entities.XQueryTask;
import at.jku.dke.task_app.xquery.evaluation.EvaluationServiceImpl;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Analyzes and prepares the evaluation of a submission.
 */
public class AnalysisImpl implements Analysis {
    private static final Logger LOG = LoggerFactory.getLogger(EvaluationServiceImpl.class);

    private final XQResult submissionResult;
    private final XQResult solutionResult;
    private final XQueryTask task;
    private final List<String> sorting;

    private boolean schemaValid;
    private List<NodeModel> missingNodes;
    private List<NodeModel> superfluousNodes;
    private List<IncorrectTextValueModel> incorrectTextValues;
    private List<NodeModel> displacedNodes;
    private List<AttributeModel> missingAttributes;
    private List<AttributeModel> superfluousAttributes;
    private List<IncorrectAttributeValueModel> incorrectAttributeValues;
    private List<String> invalidElementNames;
    private List<String> invalidAttributeNames;

    /**
     * Creates a new instance of class {@link AnalysisImpl} and analyzes the results.
     *
     * @param submissionResult The result of the submission.
     * @param solutionResult   The result of the solution.
     * @param task             The task that is checked.
     * @throws NullPointerException If {@code submissionResult} or {@code solutionResult} is {@code null}.
     * @throws AnalysisException    If an error occurs during analysis.
     */
    public AnalysisImpl(XQResult submissionResult, XQResult solutionResult, XQueryTask task) throws AnalysisException {
        Objects.requireNonNull(submissionResult);
        Objects.requireNonNull(solutionResult);

        this.submissionResult = submissionResult;
        this.solutionResult = solutionResult;
        this.schemaValid = false;
        this.task = task;
        this.sorting = task == null || task.getSorting() == null ? List.of() : task.getSorting();
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

    /**
     * Returns the list of unrecognized element names (i.e. element names not contained in the solution result).
     *
     * @return The invalid element names.
     */
    @Override
    public List<String> getInvalidElementNames() {
        return this.invalidElementNames;
    }

    /**
     * Returns the list of unrecognized attribute names (i.e. attribute names not contained in the solution result).
     *
     * @return The invalid attribute names.
     */
    @Override
    public List<String> getInvalidAttributeNames() {
        return this.invalidAttributeNames;
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

        // init lists
        this.missingNodes = new ArrayList<>();
        this.superfluousNodes = new ArrayList<>();
        this.missingAttributes = new ArrayList<>();
        this.superfluousAttributes = new ArrayList<>();
        this.incorrectAttributeValues = new ArrayList<>();
        this.incorrectTextValues = new ArrayList<>();
        this.displacedNodes = new ArrayList<>();

        // analyze
        Diff docDiff = DiffBuilder.compare(Input.fromDocument(this.solutionResult.getResultDocument()))
            .withTest(Input.fromDocument(this.submissionResult.getResultDocument()))
            .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText, ElementSelectors.byName))
            .withDifferenceEvaluator(new BruteForceDifferenceEvaluator())
            .ignoreComments()
            .ignoreWhitespace()
            .normalizeWhitespace()
            .checkForSimilar()
            .build();
        for (var diff : docDiff.getDifferences()) {
            var comparison = diff.getComparison();
            var submissionDetails = comparison.getTestDetails();
            var solutionDetails = comparison.getControlDetails();
            switch (comparison.getType()) {
                case ATTR_VALUE -> this.handleWrongAttributeValue(submissionDetails, solutionDetails);
                case TEXT_VALUE -> this.handleWrongTextValue(submissionDetails, solutionDetails);
                case CHILD_LOOKUP -> this.handleChildLookup(submissionDetails, solutionDetails);
                case ATTR_NAME_LOOKUP -> this.handleAttributeLookup(submissionDetails, solutionDetails);
            }
        }

        if (!this.incorrectAttributeValues.isEmpty()) { // try again with different matcher, if there are no matches, then only wrong sorting
            docDiff = DiffBuilder.compare(Input.fromDocument(this.solutionResult.getResultDocument()))
                .withTest(Input.fromDocument(this.submissionResult.getResultDocument()))
                .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes, ElementSelectors.byNameAndText, ElementSelectors.byName))
                .withDifferenceEvaluator(new BruteForceDifferenceEvaluator())
                .ignoreComments()
                .ignoreWhitespace()
                .normalizeWhitespace()
                .checkForSimilar()
                .build();
            boolean found = false;
            for (var diff : docDiff.getDifferences()) {
                var comparison = diff.getComparison();
                if (comparison.getType() == ComparisonType.ATTR_VALUE) {
                    found = true;
                    break;
                }
            }
            if (!found)
                this.incorrectAttributeValues.clear();
        }

        this.checkSorting();

        // check element/attribute names for run mode
        if (this.task == null) {
            this.invalidAttributeNames = List.of();
            this.invalidElementNames = List.of();
            return;
        }

        try {
            var processor = new Processor(false);
            var doc = processor.newDocumentBuilder().build(new DOMSource(this.submissionResult.getResultDocument()));

            var xpathResult = processor.newXPathCompiler().evaluate("distinct-values(//*/name())", doc);
            this.invalidElementNames = xpathResult.stream()
                .map(XdmItem::getStringValue)
                .filter(x -> !this.task.getSolutionElements().contains(x))
                .toList();

            xpathResult = processor.newXPathCompiler().evaluate("distinct-values(//*//@*/name())", doc);
            this.invalidAttributeNames = xpathResult.stream()
                .map(XdmItem::getStringValue)
                .filter(x -> !this.task.getSolutionAttributes().contains(x))
                .toList();
        } catch (SaxonApiException ex) {
            LOG.error("Could not determine elements/attributes in submitted result", ex);
        }
    }

    //#region --- Difference handlers ---

    /**
     * Handles wrong attribute values.
     *
     * @param submissionDetails The comparison details for the submission result.
     * @param solutionDetails   The comparison details for the solution result.
     * @see #getIncorrectAttributeValues()
     */
    private void handleWrongAttributeValue(Comparison.Detail submissionDetails, Comparison.Detail solutionDetails) {
        this.incorrectAttributeValues.add(new IncorrectAttributeValueModel(submissionDetails.getParentXPath(), submissionDetails.getTarget().getNodeName(), solutionDetails.getValue().toString()));
    }

    /**
     * Handles wrong text values.
     *
     * @param submissionDetails The comparison details for the submission result.
     * @param solutionDetails   The comparison details for the solution result.
     * @see #getIncorrectTextValues()
     */
    private void handleWrongTextValue(Comparison.Detail submissionDetails, Comparison.Detail solutionDetails) {
        this.incorrectTextValues.add(new IncorrectTextValueModel(submissionDetails.getParentXPath(), solutionDetails.getValue().toString()));
    }

    /**
     * Handles child lookup.
     *
     * @param submissionDetails The comparison details for the submission result.
     * @param solutionDetails   The comparison details for the solution result.
     * @see #getIncorrectTextValues()
     * @see #getMissingNodes()
     * @see #getSuperfluousNodes()
     */
    private void handleChildLookup(Comparison.Detail submissionDetails, Comparison.Detail solutionDetails) {
        if (submissionDetails.getTarget() == null) { // missing node
            if (solutionDetails.getTarget().getNodeType() == Node.TEXT_NODE)
                this.incorrectTextValues.add(new IncorrectTextValueModel(submissionDetails.getParentXPath(), solutionDetails.getTarget().getNodeValue()));
            else
                this.missingNodes.add(new NodeModel(submissionDetails.getParentXPath(), solutionDetails.getValue().toString()));
        } else if (solutionDetails.getTarget() == null) { // superfluous node
            if (submissionDetails.getTarget().getNodeType() == Node.TEXT_NODE)
                this.incorrectTextValues.add(new IncorrectTextValueModel(submissionDetails.getParentXPath(), ""));
            else
                this.superfluousNodes.add(new NodeModel(submissionDetails.getParentXPath(), submissionDetails.getValue().toString()));
        }
    }

    /**
     * Handles missing or superfluous attributes.
     *
     * @param submissionDetails The comparison details for the submission result.
     * @param solutionDetails   The comparison details for the solution result.
     * @see #getMissingAttributes()
     * @see #getSuperfluousAttributes()
     */
    private void handleAttributeLookup(Comparison.Detail submissionDetails, Comparison.Detail solutionDetails) {
        if (submissionDetails.getValue() == null) { // missing attribute
            this.missingAttributes.add(new AttributeModel(submissionDetails.getXPath(), solutionDetails.getValue().toString(), solutionDetails.getTarget().getAttributes().getNamedItem(solutionDetails.getValue().toString()).getNodeValue()));
        } else if (solutionDetails.getValue() == null) { // superfluous attribute
            this.superfluousAttributes.add(new AttributeModel(submissionDetails.getParentXPath(), submissionDetails.getValue().toString(), submissionDetails.getTarget().getAttributes().getNamedItem(submissionDetails.getValue().toString()).getNodeValue()));
        }
    }

    //#endregion

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
                    continue; // do not check, something else is wrong

                for (int i = 0; i < submissionList.getLength(); i++) {
                    Node submissionNode = submissionList.item(i);
                    Node solutionNode = solutionList.item(i);

                    if (!submissionNode.isEqualNode(solutionNode)) {
                        this.displacedNodes.add(new NodeModel(expression, buildXPath(submissionNode)));
                    }
                }
            }
        } catch (XPathExpressionException ex) {
            LOG.error("Could not compile XPath expression.", ex);
            throw new AnalysisException("A fatal error occurred while analyzing the order of the result elements.", ex);
        }
    }

    /**
     * Generates the XPath expression to the specified node.
     *
     * @param xpathNode The node.
     * @return The XPath expression to the node.
     */
    private static String buildXPath(Node xpathNode) {
        List<String> elements = new ArrayList<>();

        int precedingSiblings = 0;
        Node preceding = xpathNode.getPreviousSibling();
        while (preceding != null) {
            if (preceding.getNodeType() == Node.ELEMENT_NODE && preceding.getNodeName().equals(xpathNode.getNodeName()))
                precedingSiblings++;
            preceding = preceding.getPreviousSibling();
        }

        elements.add(xpathNode.getNodeName() + "[" + (precedingSiblings + 1) + "]");


        Node parent = xpathNode.getParentNode();
        while (parent != null) {
            if (parent.getNodeName().equals("xquery-result"))
                break;

            precedingSiblings = 0;
            preceding = parent.getPreviousSibling();
            while (preceding != null) {
                if (preceding.getNodeType() == Node.ELEMENT_NODE && preceding.getNodeName().equals(parent.getNodeName()))
                    precedingSiblings++;
                preceding = preceding.getPreviousSibling();
            }

            elements.add(parent.getNodeName() + "[" + (precedingSiblings + 1) + "]");
            parent = parent.getParentNode();
        }

        return '/' + String.join("/", elements.reversed());
    }

    /**
     * Validates the sorting XPath expressions against the solution.
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
