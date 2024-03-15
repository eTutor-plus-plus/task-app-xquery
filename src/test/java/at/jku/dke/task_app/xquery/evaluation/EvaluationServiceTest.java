package at.jku.dke.task_app.xquery.evaluation;

import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;
import at.jku.dke.etutor.task_app.dto.TaskStatus;
import at.jku.dke.task_app.xquery.config.XQuerySettings;
import at.jku.dke.task_app.xquery.data.entities.XQueryTask;
import at.jku.dke.task_app.xquery.data.entities.XQueryTaskGroup;
import at.jku.dke.task_app.xquery.data.repositories.XQueryTaskRepository;
import at.jku.dke.task_app.xquery.dto.XQuerySubmissionDto;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EvaluationServiceTest {

    @Test
    void evaluate_invalidLoad() {
        // Arrange
        var taskRepository = mock(XQueryTaskRepository.class);
        var settings = new XQuerySettings("basex", "./basex");
        var ms = mock(MessageSource.class);
        var service = new EvaluationServiceImpl(settings, taskRepository, ms);

        var group = new XQueryTaskGroup(1L, TaskStatus.APPROVED, DIAGNOSE, SUBMIT);
        var task = new XQueryTask(1L, BigDecimal.ONE, TaskStatus.APPROVED, group, "return doc('etutor.xml')/db", null);
        when(taskRepository.findByIdWithTaskGroup(1L)).thenReturn(Optional.of(task));
        when(ms.getMessage(anyString(), any(), any())).thenAnswer(i -> i.getArgument(0));

        // Act
        var result = service.evaluate(new SubmitSubmissionDto<>(null, null, 1L, "de", SubmissionMode.SUBMIT, 0,
            new XQuerySubmissionDto("return doc('some-file.xml')/db")));

        // Assert
        assertEquals(result.points(), BigDecimal.ZERO);
        assertEquals(result.generalFeedback(), "syntaxError");
        assertEquals(result.criteria().size(), 1);
        assertEquals(result.criteria().getFirst().feedback(), "invalidDocument");
    }

    @Test
    void evaluate_invalidSubmissionSyntax() {
        // Arrange
        var taskRepository = mock(XQueryTaskRepository.class);
        var settings = new XQuerySettings("basex", "./basex");
        var ms = mock(MessageSource.class);
        var service = new EvaluationServiceImpl(settings, taskRepository, ms);

        var group = new XQueryTaskGroup(1L, TaskStatus.APPROVED, DIAGNOSE, SUBMIT);
        var task = new XQueryTask(1L, BigDecimal.ONE, TaskStatus.APPROVED, group, "return doc('etutor.xml')/db", null);
        when(taskRepository.findByIdWithTaskGroup(1L)).thenReturn(Optional.of(task));
        when(ms.getMessage(anyString(), any(), any())).thenAnswer(i -> i.getArgument(0));

        // Act
        var result = service.evaluate(new SubmitSubmissionDto<>(null, null, 1L, "de", SubmissionMode.SUBMIT, 0,
            new XQuerySubmissionDto("return doc('etutor.xml')/db@+1")));

        // Assert
        assertEquals(result.points(), BigDecimal.ZERO);
        assertEquals(result.generalFeedback(), "syntaxError");
        assertEquals(result.criteria().size(), 1);
        assertNotNull(result.criteria().getFirst().feedback());
    }

    @Test
    void evaluate() {
        // Arrange
        var taskRepository = mock(XQueryTaskRepository.class);
        var settings = new XQuerySettings("basex", "./basex");
        var ms = mock(MessageSource.class);
        var service = new EvaluationServiceImpl(settings, taskRepository, ms);

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
        assertEquals(BigDecimal.ZERO, result.points());
        assertEquals(4, result.criteria().size());
    }

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
