package at.jku.dke.task_app.xquery.evaluation.analysis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class AnalysisTest {

    @Test
    void getSubmissionResult() throws AnalysisException {
        fail("Not yet implemented");
    }

    @Test
    void getSolutionResult() throws AnalysisException {
        fail("Not yet implemented");
    }

    @Test
    void isSchemaValid() throws AnalysisException {
        fail("Not yet implemented");
    }

    @Test
    void compare_missingNodes() throws AnalysisException {
        // Arrange
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

        // Act
        var analysis = new Analysis(new XQResult(submission), new XQResult(solution));
        var result = analysis.getMissingNodes();

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void compare_missingNodesNested() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
                <root>
                    <child></child>
                    <child>
                        <subchild></subchild>
                    </child>
                </root>
            </xquery-result>
            """;
        var solution = """
            <xquery-result>
                <root>
                    <child></child>
                    <child>
                        <subchild></subchild>
                        <subchild></subchild>
                    </child>
                </root>
            </xquery-result>
            """;

        // Act
        var analysis = new Analysis(new XQResult(submission), new XQResult(solution));
        var result = analysis.getMissingNodes();

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void compare_superfluousNodes() throws AnalysisException {
        // Arrange
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

        // Act
        var analysis = new Analysis(new XQResult(submission), new XQResult(solution));
        var result = analysis.getSuperfluousNodes();

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void compare_missingAttributes() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
                <root>
                    <child id="1"></child>
                    <child></child>
                </root>
            </xquery-result>
            """;
        var solution = """
            <xquery-result>
                <root>
                    <child id="1"></child>
                    <child id="2" name="test"></child>
                </root>
            </xquery-result>
            """;

        // Act
        var analysis = new Analysis(new XQResult(submission), new XQResult(solution));
        var result = analysis.getMissingAttributes();

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void compare_superfluousAttributes() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
                <root>
                    <child id="1"></child>
                    <child id="2"></child>
                </root>
            </xquery-result>
            """;
        var solution = """
            <xquery-result>
                <root>
                    <child id="1"></child>
                    <child></child>
                </root>
            </xquery-result>
            """;

        // Act
        var analysis = new Analysis(new XQResult(submission), new XQResult(solution));
        var result = analysis.getSuperfluousAttributes();

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void compare_incorrectAttributeValue() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
                <root>
                    <child id="2"></child>
                    <child></child>
                    <child2 id="1"></child2>
                    <sub>
                        <child id="1"></child>
                    </sub>
                </root>
            </xquery-result>
            """;
        var solution = """
            <xquery-result>
                <root>
                    <child id="1"></child>
                    <child></child>
                    <child2 id="2"></child2>
                    <sub>
                        <child id="2"></child>
                    </sub>
                </root>
            </xquery-result>
            """;

        // Act
        var analysis = new Analysis(new XQResult(submission), new XQResult(solution));
        var result = analysis.getIncorrectAttributeValues();

        // Assert
        assertEquals(3, result.size());
    }

    @Test
    void compare_incorrectTextValueReplace() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
                <root>
                    <child>1</child>
                    <child>2</child>
                    <sub>
                        <child>3</child>
                    </sub>
                </root>
            </xquery-result>
            """;
        var solution = """
            <xquery-result>
                <root>
                    <child>1</child>
                    <child>3</child>
                    <sub>
                        <child>2</child>
                    </sub>
                </root>
            </xquery-result>
            """;

        // Act
        var analysis = new Analysis(new XQResult(submission), new XQResult(solution));
        var result = analysis.getIncorrectTextValues();

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void compare_incorrectTextValueInsert() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
                <root>
                    <child>1</child>
                    <child></child>
                </root>
            </xquery-result>
            """;
        var solution = """
            <xquery-result>
                <root>
                    <child>1</child>
                    <child>3</child>
                </root>
            </xquery-result>
            """;

        // Act
        var analysis = new Analysis(new XQResult(submission), new XQResult(solution));
        var result = analysis.getIncorrectTextValues();

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void compare_incorrectTextValueRemove() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
                <root>
                    <child>1</child>
                    <child>2</child>
                </root>
            </xquery-result>
            """;
        var solution = """
            <xquery-result>
                <root>
                    <child>1</child>
                    <child></child>
                </root>
            </xquery-result>
            """;

        // Act
        var analysis = new Analysis(new XQResult(submission), new XQResult(solution));
        var result = analysis.getIncorrectTextValues();

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void compare_displacedNodesWithValues() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
                <root>
                    <child>1</child>
                    <child>2</child>
                    <child2>1</child2>
                    <child2>2</child2>
                </root>
            </xquery-result>
            """;
        var solution = """
            <xquery-result>
                <root>
                    <child>2</child>
                    <child>1</child>
                    <child2>2</child2>
                    <child2>1</child2>
                </root>
            </xquery-result>
            """;

        // Act
        var analysis = new Analysis(new XQResult(submission), new XQResult(solution));
        var shouldBeEmpty = analysis.getIncorrectTextValues();
        var result = analysis.getDisplacedNodes();

        // Assert
        assertEquals(0, shouldBeEmpty.size());
        assertEquals(2, result.size());
    }

    @Test
    void compare_displacedNodesWithAttributes() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
                <root>
                    <child id="1"></child>
                    <child id="2"></child>
                    <child2 id="1"></child2>
                    <child2 id="2"></child2>
                </root>
            </xquery-result>
            """;
        var solution = """
            <xquery-result>
                <root>
                    <child id="2"></child>
                    <child id="1"></child>
                    <child2 id="2"></child2>
                    <child2 id="1"></child2>
                </root>
            </xquery-result>
            """;

        // Act
        var analysis = new Analysis(new XQResult(submission), new XQResult(solution));
        var shouldBeEmpty = analysis.getIncorrectAttributeValues();
        var result = analysis.getDisplacedNodes();

        // Assert
        assertEquals(0, shouldBeEmpty.size());
        assertEquals(2, result.size());
    }
}
