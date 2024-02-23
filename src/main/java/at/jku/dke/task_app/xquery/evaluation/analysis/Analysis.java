package at.jku.dke.task_app.xquery.evaluation.analysis;

import at.jku.dke.task_app.xquery.evaluation.EvaluationService;
import net.sf.saxon.s9api.*;
import org.pageseeder.diffx.DiffException;
import org.pageseeder.diffx.Main;
import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Analyzes and prepares the evaluation of a submission.
 */
public class Analysis {
    private static final Logger LOG = LoggerFactory.getLogger(EvaluationService.class);

    private final String submissionResult;
    private final String solutionResult;

    private List<Object> missingNodes; // im Ergebnis der Musterlösung enthalten, aber im Ergebnis der Abgabe nicht enthalten
    private List<Object> superfluousNodes; // umgekehrter fall von missingNodes
    private List<Object> displacedNodes; // die zwar auf einer bestimmten Hierarchiestufe in der XML-Struktur korrekt vorhanden sind, allerdings nicht an der erwarteten Position
    private List<Object> missingAttributes; // fehlende Attribute
    private List<Object> superfluousAttributes; // überflüssige Attribute
    private List<Object> incorrectAttributeValues; // falsche Attributwerte

    /*private List<Object> redundantNodes;
    private List<Object> redundantInsteadNodes;
    private List<Object> displacedNodes;
    private List<Object> missingInsteadNodes;
    private List<Object> missingPreviousNodes;
    private List<Object> missingNextNodes;
    private List<Object> missingInnerNodes;
    private List<Object> redundantAttributes;
    private List<Object> incorrectAttributeValues;
    private List<Object> missingAttributes;
    private List<Object> incorrectTextValues;*/

    /**
     * Creates a new instance of class {@link Analysis} and analyzes the results.
     *
     * @param submissionResult The result of the submission.
     * @param solutionResult   The result of the solution.
     */
    public Analysis(String submissionResult, String solutionResult) {
        Objects.requireNonNull(submissionResult);
        Objects.requireNonNull(solutionResult);

        this.submissionResult = submissionResult;
        this.solutionResult = solutionResult;
        this.analyze();
    }

    /**
     * Analyzes the results.
     */
    private void analyze() {
        String diff = this.generateDiff();
        String analysis = this.transformDiffToAnalysis(diff);
    }

    /**
     * Generates the diff between the submission and the solution.
     *
     * @return The diff as XML.
     * @throws RuntimeException If the diff could not be generated.
     */
    private String generateDiff() {
        try (var submissionReader = new StringReader(this.submissionResult);
             var solutionReader = new StringReader(this.solutionResult);
             var writer = new StringWriter()) {
            Main.diff(submissionReader, solutionReader, writer, new DiffConfig(false, WhiteSpaceProcessing.IGNORE, TextGranularity.TEXT));

            String diffXml = writer.toString();
            Files.writeString(Path.of("xml-documents", "diff.xml"), diffXml); // TODO: remove, only here for debugging
            return diffXml;
        } catch (IOException | DiffException ex) {
            LOG.error("Could not generate diff.", ex);
            throw new RuntimeException(ex);
        }
    }

    private String transformDiffToAnalysis(String diffXml) {
        try (var writer = new StringWriter();
             var reader = new StringReader(diffXml);
//             var xslt = this.getClass().getClassLoader().getResourceAsStream("transform.xslt")) {
             var xslt = new FileInputStream("/Users/martin/Development/dke/etutor_neu/task-app-xquery/src/main/resources/transform.xslt")) {
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
            Files.writeString(Path.of("xml-documents", "diff-transformed.xml"), result); // TODO: remove, only here for debugging
            return result;
        } catch (IOException | SaxonApiException e) {
            throw new RuntimeException(e);
        }
    }
}
// https://github.com/codelibs/jhighlight
