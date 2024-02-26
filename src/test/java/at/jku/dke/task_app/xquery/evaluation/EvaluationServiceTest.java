package at.jku.dke.task_app.xquery.evaluation;

import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;
import at.jku.dke.etutor.task_app.dto.TaskStatus;
import at.jku.dke.task_app.xquery.config.XQuerySettings;
import at.jku.dke.task_app.xquery.data.entities.XQueryTask;
import at.jku.dke.task_app.xquery.data.entities.XQueryTaskGroup;
import at.jku.dke.task_app.xquery.data.repositories.XQueryTaskRepository;
import at.jku.dke.task_app.xquery.dto.XQuerySubmissionDto;
import net.sf.saxon.s9api.*;
import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.DiffException;
import org.pageseeder.diffx.Main;
import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.springframework.context.MessageSource;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EvaluationServiceTest {

    @Test
    void evaluate() {
        // Arrange
        var taskRepository = mock(XQueryTaskRepository.class);
        var settings = new XQuerySettings("basex", "./basex");
        var ms = mock(MessageSource.class);
        var service = new EvaluationService(settings, taskRepository, ms);

        var group = new XQueryTaskGroup(3L, TaskStatus.APPROVED, DIAGNOSE, SUBMIT);
        var task = new XQueryTask(2L, BigDecimal.ONE, TaskStatus.APPROVED, group, """
            let $db := doc('etutor.xml')/db,
                $personen := $db/person, $wohnungen := $db/wohnung,
                $mietet := $db/mietet
            return let $ma := $mietet[bis eq '31.12.2099']
                   let $qms := (for $m in $ma, $w in $wohnungen[@nr eq $m/@wohnnr]
                                return ($m/preis div $w/gross))
            return
             <mietstatistik>
              <anzahl>{count($ma)}</anzahl>
              <sum-preis>{sum($ma/preis)}</sum-preis>
              <qm-preis>{avg($qms)}</qm-preis>
             </mietstatistik>""", null);
        when(taskRepository.findByIdWithTaskGroup(2L)).thenReturn(Optional.of(task));
        when(ms.getMessage(anyString(), any(), any())).thenReturn("unknown");

        // Act
        var result = service.evaluate(new SubmitSubmissionDto<>(null, null, 2L, "de", SubmissionMode.DIAGNOSE, 3,
            new XQuerySubmissionDto("""
                let $db := doc('etutor.xml')/db,
                    $personen := $db/person, $wohnungen := $db/wohnung,
                    $mietet := $db/mietet
                return let $ma := $mietet[bis eq '31.12.2099']
                       let $qms := (for $m in $ma, $w in $wohnungen[@nr eq $m/@wohnnr]
                                    return ($m/preis div $w/gross))
                return
                 <mietstatistik>
                  <sum-preis>{sum($ma/preis)}</sum-preis>
                  <qm-preis>{avg($qms)}</qm-preis>
                 </mietstatistik>""")));

        // Assert
        fail("Not implemented yet");
    }

    @Test
    void diff1_missingNode() throws DiffException, IOException {
        var submission = """
            <xquery-result>
                <root>
                    <child></child>
                </root>
            </xquery-result>
            """;
        var solution = """
            <xquery-result>
                <root>
                    <child></child>
                    <child></child>
                </root>
            </xquery-result>
            """;

        exec(submission, solution, 1);
    }

    @Test
    void diff2_superfluousNode() throws DiffException, IOException {
        var submission = """
            <xquery-result>
                <root>
                    <child></child>
                    <child></child>
                </root>
            </xquery-result>
            """;
        var solution = """
            <xquery-result>
                <root>
                    <child></child>
                </root>
            </xquery-result>
            """;

        exec(submission, solution, 2);
    }

    @Test
    void diff3_wrongValue() throws DiffException, IOException {
        var submission = """
            <xquery-result>
                <root>
                    <child>value1</child>
                    <child>value2</child>
                </root>
            </xquery-result>
            """;
        var solution = """
            <xquery-result>
                <root>
                    <child>value1</child>
                    <child>value3</child>
                </root>
            </xquery-result>
            """;

        exec(submission, solution, 3);
    }

    private void exec(String sub, String sol, int id) throws IOException, DiffException {
        String diffXml;
        try (var submissionReader = new StringReader(sub);
             var solutionReader = new StringReader(sol);
             var writer = new StringWriter()) {
            Main.diff(submissionReader, solutionReader, writer, new DiffConfig(false, WhiteSpaceProcessing.IGNORE, TextGranularity.TEXT));

            diffXml = writer.toString();
            Files.writeString(Path.of("xml-documents", "diff" + id + ".xml"), diffXml);
        }

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
            Files.writeString(Path.of("xml-documents", "diff-" + id + "-transformed.xml"), result);
        } catch (IOException | SaxonApiException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: is wellformed wenn gleiches Attribut mehrmals vorkommt?

    private static final String DIAGNOSE = """
        <db>
             <person nr="p1">
                 <name>Deckere</name>
                 <stand>ledig</stand>
                 <beruf>Student</beruf>
             </person>
             <person nr="p2">
                 <name>Meier</name>
                 <stand>verheiratet</stand>
                 <beruf>Maurer</beruf>
             </person>
             <person nr="p3">
                 <name>Huber</name>
                 <stand>ledig</stand>
                 <beruf>Schlosser</beruf>
             </person>
             <person nr="p4">
                 <name>Bauer</name>
                 <stand>verwitwet</stand>
                 <beruf>Beamter</beruf>
             </person>
             <person nr="p5">
                 <name>Kaiser</name>
                 <stand>verheiratet</stand>
                 <beruf>Beamter</beruf>
             </person>
             <person nr="p6">
                 <name>Richter</name>
                 <stand>ledig</stand>
                 <beruf>Anwalt</beruf>
             </person>
             <person nr="p7">
                 <name>Weiss</name>
                 <stand>ledig</stand>
                 <beruf>Maler</beruf>
             </person>
             <person nr="p8">
                 <name>Traxler</name>
                 <stand>verheiratet</stand>
                 <beruf>Student</beruf>
             </person>
             <person nr="p9">
                 <name>Seyfried</name>
                 <stand>ledig</stand>
                 <beruf>Maurer</beruf>
             </person>
             <person nr="p10">
                 <name>Weikinger</name>
                 <stand>ledig</stand>
                 <beruf>Lehrer</beruf>
             </person>
             <person nr="p11">
                 <name>Rechberger</name>
                 <stand>verheiratet</stand>
                 <beruf>Hausmeister</beruf>
             </person>
             <person nr="p12">
                 <name>Gangl</name>
                 <stand>ledig</stand>
                 <beruf>Hausmeister</beruf>
             </person>
             <person nr="p13">
                 <name>Wallner</name>
                 <stand>verwitwet</stand>
                 <beruf>Beamter</beruf>
             </person>
             <person nr="p14">
                 <name>Reiber</name>
                 <stand>ledig</stand>
                 <beruf>Student</beruf>
             </person>
             <mietet mieternr="p1" wohnnr="w2">
                 <preis>500</preis>
                 <von>01.01.1996</von>
                 <bis>31.08.1999</bis>
             </mietet>
             <mietet mieternr="p8" wohnnr="w2">
                 <preis>900</preis>
                 <von>01.10.1999</von>
                 <bis>31.12.2001</bis>
             </mietet>
             <mietet mieternr="p2" wohnnr="w7">
                 <preis>950</preis>
                 <von>01.01.1997</von>
                 <bis>31.12.2099</bis>
             </mietet>
             <mietet mieternr="p6" wohnnr="w9">
                 <preis>1500</preis>
                 <von>01.04.1994</von>
                 <bis>30.09.1999</bis>
             </mietet>
             <mietet mieternr="p11" wohnnr="w9">
                 <preis>1600</preis>
                 <von>01.10.1999</von>
                 <bis>31.12.2099</bis>
             </mietet>
             <mietet mieternr="p9" wohnnr="w4">
                 <preis>700</preis>
                 <von>01.01.1997</von>
                 <bis>28.02.1999</bis>
             </mietet>
             <mietet mieternr="p11" wohnnr="w4">
                 <preis>650</preis>
                 <von>01.03.1999</von>
                 <bis>31.12.2099</bis>
             </mietet>
             <mietet mieternr="p5" wohnnr="w3">
                 <preis>1100</preis>
                 <von>01.01.1996</von>
                 <bis>31.12.2099</bis>
             </mietet>
             <mietet mieternr="p8" wohnnr="w11">
                 <preis>1100</preis>
                 <von>01.05.1998</von>
                 <bis>30.09.1999</bis>
             </mietet>
             <mietet mieternr="p9" wohnnr="w11">
                 <preis>1200</preis>
                 <von>01.01.2000</von>
                 <bis>30.09.2001</bis>
             </mietet>
             <mietet mieternr="p9" wohnnr="w1">
                 <preis>950</preis>
                 <von>01.01.1996</von>
                 <bis>31.12.2099</bis>
             </mietet>
             <mietet mieternr="p11" wohnnr="w6">
                 <preis>1400</preis>
                 <von>01.01.1996</von>
                 <bis>01.08.1999</bis>
             </mietet>
             <mietet mieternr="p1" wohnnr="w13">
                 <preis>2100</preis>
                 <von>01.01.1990</von>
                 <bis>31.12.1997</bis>
             </mietet>
             <mietet mieternr="p9" wohnnr="w13">
                 <preis>2200</preis>
                 <von>01.01.1998</von>
                 <bis>31.12.2099</bis>
             </mietet>
             <mietet mieternr="p13" wohnnr="w5">
                 <preis>1000</preis>
                 <von>01.01.1996</von>
                 <bis>31.08.2001</bis>
             </mietet>
             <mietet mieternr="p14" wohnnr="w12">
                 <preis>1200</preis>
                 <von>01.01.1996</von>
                 <bis>31.01.2002</bis>
             </mietet>
             <wohnung nr="w1" eigentuemer="p6">
                 <bezirk>4</bezirk>
                 <gross>62</gross>
             </wohnung>
             <wohnung nr="w2" eigentuemer="p6">
                 <bezirk>1</bezirk>
                 <gross>100</gross>
             </wohnung>
             <wohnung nr="w3" eigentuemer="p4">
                 <bezirk>1</bezirk>
                 <gross>60</gross>
             </wohnung>
             <wohnung nr="w4" eigentuemer="p3">
                 <bezirk>2</bezirk>
                 <gross>80</gross>
             </wohnung>
             <wohnung nr="w5" eigentuemer="p7">
                 <bezirk>5</bezirk>
                 <gross>40</gross>
             </wohnung>
             <wohnung nr="w6" eigentuemer="p3">
                 <bezirk>3</bezirk>
                 <gross>100</gross>
             </wohnung>
             <wohnung nr="w7" eigentuemer="p5">
                 <bezirk>4</bezirk>
                 <gross>100</gross>
             </wohnung>
             <wohnung nr="w8" eigentuemer="p9">
                 <bezirk>5</bezirk>
                 <gross>40</gross>
             </wohnung>
             <wohnung nr="w9" eigentuemer="p10">
                 <bezirk>5</bezirk>
                 <gross>100</gross>
             </wohnung>
             <wohnung nr="w10" eigentuemer="p4">
                 <bezirk>3</bezirk>
                 <gross>30</gross>
             </wohnung>
             <wohnung nr="w11" eigentuemer="p7">
                 <bezirk>3</bezirk>
                 <gross>95</gross>
             </wohnung>
             <wohnung nr="w12" eigentuemer="p9">
                 <bezirk>3</bezirk>
                 <gross>50</gross>
             </wohnung>
             <wohnung nr="w13" eigentuemer="p10">
                 <bezirk>4</bezirk>
                 <gross>120</gross>
             </wohnung>
         </db>""";

    private static final String SUBMIT = DIAGNOSE;

}
