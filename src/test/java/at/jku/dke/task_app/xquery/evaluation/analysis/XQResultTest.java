package at.jku.dke.task_app.xquery.evaluation.analysis;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class XQResultTest {

    @Test
    void getRawResult() throws AnalysisException {
        // Arrange
        var xml = """
            <root>
                <child>test</child>
            </root>""";
        var xqResult = new XQResult(xml);

        // Act
        var result = xqResult.getRawResult();

        // Assert
        assertEquals(xml, result);
    }

    @Test
    void getResultDocument_validXml() throws AnalysisException {
        // Arrange
        var xml = """
            <root>
                <child>test</child>
            </root>""";
        var xqResult = new XQResult(xml);

        // Act
        var result = xqResult.getResultDocument();

        // Assert
        assertNotNull(result);
        assertEquals(XQResult.XML_ROOT, result.getDocumentElement().getTagName());
    }

    @Test
    void getResultDocument_invalidXml() throws AnalysisException {
        // Arrange
        var xml = """
            <root>
                <child>test</child
            </root>""";
        var xqResult = new XQResult(xml);

        // Act
        var result = xqResult.getResultDocument();

        // Assert
        assertNull(result);
    }

    @Test
    void getParseException_validXml() throws AnalysisException {
        // Arrange
        var xml = """
            <root>
                <child>test</child>
            </root>""";
        var xqResult = new XQResult(xml);

        // Act
        var result = xqResult.getParseException();

        // Assert
        assertNull(result);
    }

    @Test
    void getParseException_invalidXml() throws AnalysisException {
        // Arrange
        var xml = """
            <root>
                <child>test</child
            </root>""";
        var xqResult = new XQResult(xml);

        // Act
        var result = xqResult.getParseException();

        // Assert
        assertNotNull(result);
    }

    @Test
    void getResultFile_generate() throws AnalysisException {
        // Arrange
        var xml = """
            <root>
                <child>test</child>
            </root>""";
        var xqResult = new XQResult(xml);

        // Act
        var result = xqResult.getResultFile();

        // Assert
        assertNotNull(result);
        assertThat(result)
            .exists()
            .isNotEmptyFile()
            .content()
            .contains("test")
            .contains("child");
    }

    @Test
    void getResultFile_regenerate() throws AnalysisException {
        // Arrange
        var xml = """
            <root>
                <child>test</child>
            </root>""";
        var xqResult = new XQResult(xml);

        // Act
        var result = xqResult.getResultFile();
        var lastModified = result.toFile().lastModified();
        result = xqResult.getResultFile(true);

        // Assert
        assertNotEquals(lastModified, result.toFile().lastModified());
    }

    @Test
    void getResultFile_dontRegenerate() throws AnalysisException {
        // Arrange
        var xml = """
            <root>
                <child>test</child>
            </root>""";
        var xqResult = new XQResult(xml);

        // Act
        var result = xqResult.getResultFile();
        var lastModified = result.toFile().lastModified();
        result = xqResult.getResultFile();

        // Assert
        assertEquals(lastModified, result.toFile().lastModified());
    }

    @Test
    void getResultFile_invalidXml() throws AnalysisException {
        // Arrange
        var xml = """
            <root>
                <child>test</child
            </root>""";
        var xqResult = new XQResult(xml);

        // Act
        var result = xqResult.getResultFile();

        // Assert
        assertNotNull(result);
        assertThat(result)
            .exists()
            .isNotEmptyFile()
            .content()
            .isEqualTo(xml);
    }

    @Test
    void getDTDFile_generate() throws AnalysisException {
        // Arrange
        var xml = """
            <mietstatistik>
                <sum-preis>7450</sum-preis>
                <qm-preis>14.269041218637993</qm-preis>
                <other>
                    <child>1</child>
                </other>
            </mietstatistik>""";
        var xqResult = new XQResult(xml);

        // Act
        var result = xqResult.getDTDFile();

        // Assert
        assertNotNull(result);
        assertThat(result)
            .exists()
            .isNotEmptyFile()
            .content()
            .containsIgnoringWhitespaces("<!ELEMENT xquery-result (mietstatistik)>")
            .containsIgnoringWhitespaces("<!ELEMENT mietstatistik (sum-preis,qm-preis,other)>")
            .containsIgnoringWhitespaces("<!ELEMENT sum-preis (#PCDATA)>")
            .containsIgnoringWhitespaces("<!ELEMENT qm-preis (#PCDATA)>")
            .containsIgnoringWhitespaces("<!ELEMENT other (child)>")
            .containsIgnoringWhitespaces("<!ELEMENT child (#PCDATA)>");
    }

    @Test
    void getDTDFile_regenerate() throws AnalysisException {
        // Arrange
        var xml = """
            <mietstatistik>
                <sum-preis>7450</sum-preis>
                <qm-preis>14.269041218637993</qm-preis>
                <other>
                    <child>1</child>
                </other>
            </mietstatistik>""";
        var xqResult = new XQResult(xml);

        // Act
        var result = xqResult.getDTDFile();
        var lastModified = result.toFile().lastModified();
        result = xqResult.getDTDFile(true);

        // Assert
        assertNotEquals(lastModified, result.toFile().lastModified());
    }

    @Test
    void getDTDFile_dontRegenerate() throws AnalysisException {
        // Arrange
        var xml = """
            <mietstatistik>
                <sum-preis>7450</sum-preis>
                <qm-preis>14.269041218637993</qm-preis>
                <other>
                    <child>1</child>
                </other>
            </mietstatistik>""";
        var xqResult = new XQResult(xml);

        // Act
        var result = xqResult.getDTDFile();
        var lastModified = result.toFile().lastModified();
        result = xqResult.getDTDFile(false);

        // Assert
        assertEquals(lastModified, result.toFile().lastModified());
    }

    @Test
    void getDTDFile_invalidXml() throws AnalysisException {
        // Arrange
        var xml = """
            <root>
                <child>test</child
            </root>""";
        var xqResult = new XQResult(xml);

        // Act & Assert
        assertThrows(AnalysisException.class, xqResult::getDTDFile);
    }

}
