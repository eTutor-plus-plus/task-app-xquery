package at.jku.dke.task_app.xquery.evaluation.analysis;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AnalysisImplTest {

    @Test
    void getSubmissionResult() throws AnalysisException {
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
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), null);
        var result = analysis.getSubmissionResult();

        // Assert
        assertEquals(submission, result.getRawResult());
    }

    @Test
    void getSolutionResult() throws AnalysisException {
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
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), null);
        var result = analysis.getSolutionResult();

        // Assert
        assertEquals(solution, result.getRawResult());
    }

    @Test
    void isSchemaValid_sameSchema() throws AnalysisException {
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
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), null);
        var result = analysis.isSchemaValid();

        // Assert
        assertTrue(result);
    }

    @Test
    void isSchemaValid_differentSchema() throws AnalysisException {
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
                    <sub>
                        <child></child>
                        <child></child>
                    </sub>
                </root>
            </xquery-result>
            """;

        // Act
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), null);
        var result = analysis.isSchemaValid();

        // Assert
        assertFalse(result);
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
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), null);
        var result = analysis.getMissingNodes();

        // Assert
        assertThat(result)
            .hasSize(1)
            .anyMatch(node -> node.getPath().equals("/root[1]") && node.getName().equals("child"));
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
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), null);
        var result = analysis.getMissingNodes();

        // Assert
        assertThat(result)
            .hasSize(1)
            .anyMatch(node -> node.getPath().equals("/root[1]/child[2]") && node.getName().equals("subchild"));
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
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), null);
        var result = analysis.getSuperfluousNodes();

        // Assert
        assertThat(result)
            .hasSize(1)
            .anyMatch(node -> node.getPath().equals("/root[1]") && node.getName().equals("child"));
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
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), null);
        var result = analysis.getMissingAttributes();

        // Assert
        assertThat(result)
            .hasSize(2)
            .anyMatch(node -> node.getPath().equals("/root[1]/child[2]") && node.getName().equals("id") && node.getValue().equals("2"))
            .anyMatch(node -> node.getPath().equals("/root[1]/child[2]") && node.getName().equals("name") && node.getValue().equals("test"));
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
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), null);
        var result = analysis.getSuperfluousAttributes();

        // Assert
        assertThat(result)
            .hasSize(1)
            .anyMatch(node -> node.getPath().equals("/root[1]/child[2]") && node.getName().equals("id") && node.getValue().equals("2"));
    }

    @Test
    void compare_incorrectAttributeValue() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
                <root>
                    <child id="3"></child>
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
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), null);
        var result = analysis.getIncorrectAttributeValues();

        // Assert
        assertThat(result)
            .hasSize(3)
            .anyMatch(node -> node.getPath().equals("/root[1]/child[1]") && node.getName().equals("id") && node.getExpectedValue().equals("1"))
            .anyMatch(node -> node.getPath().equals("/root[1]/child2[3]") && node.getName().equals("id") && node.getExpectedValue().equals("2"))
            .anyMatch(node -> node.getPath().equals("/root[1]/sub[4]/child[1]") && node.getName().equals("id") && node.getExpectedValue().equals("2"));
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
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), null);
        var result = analysis.getIncorrectTextValues();

        // Assert
        assertThat(result)
            .hasSize(2)
            .anyMatch(node -> node.getPath().equals("/root[1]/child[2]") && node.getExpectedValue().equals("3"))
            .anyMatch(node -> node.getPath().equals("/root[1]/sub[3]/child[1]") && node.getExpectedValue().equals("2"));
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
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), null);
        var result = analysis.getIncorrectTextValues();

        // Assert
        assertThat(result)
            .hasSize(1)
            .anyMatch(node -> node.getPath().equals("/root[1]/child[2]") && node.getExpectedValue().equals("3"));
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
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), null);
        var result = analysis.getIncorrectTextValues();

        // Assert
        assertThat(result)
            .hasSize(1)
            .anyMatch(node -> node.getPath().equals("/root[1]/child[2]") && node.getExpectedValue().isEmpty());
    }

    @Test
    void compare_displacedNodesWithValues() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
                <root>
                    <child>1</child>
                    <child>2</child>
                    <sub>
                        <child2>2</child2>
                        <child2>1</child2>
                    </sub>
                    <tmp></tmp>
                </root>
            </xquery-result>
            """;
        var solution = """
            <xquery-result>
                <root>
                    <tmp></tmp>
                    <child>1</child>
                    <child>2</child>
                    <sub>
                        <child2>1</child2>
                        <child2>2</child2>
                    </sub>
                </root>
            </xquery-result>
            """;
        var sorting = List.of("//child", "//child2");

        // Act
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), sorting);
        var missingNodes = analysis.getMissingNodes();
        var superfluousNodes = analysis.getSuperfluousNodes();
        var incorrectTextValues = analysis.getIncorrectTextValues();
        var result = analysis.getDisplacedNodes();

        // Assert
        assertEquals(0, missingNodes.size());
        assertEquals(0, superfluousNodes.size());
        assertEquals(0, incorrectTextValues.size());
        assertThat(result)
            .hasSize(2)
            .allMatch(node -> node.getPath().equals("//child2") && node.getName().equals("child2"));
    }

    @Test
    void compare_displacedNodesWithAttributes() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
                <root>
                    <tmp id="1"></tmp>
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
                    <child2 id="1"></child2>
                    <child2 id="2"></child2>
                    <tmp id="1"></tmp>
                </root>
            </xquery-result>
            """;
        var sorting = List.of("//child");

        // Act
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), sorting);
        var missingAttributes = analysis.getMissingAttributes();
        var superfluousAttributes = analysis.getSuperfluousAttributes();
        var incorrectTextValues = analysis.getIncorrectTextValues();
        var incorrectAttributeValues = analysis.getIncorrectAttributeValues();
        var result = analysis.getDisplacedNodes();

        // Assert
        assertEquals(0, missingAttributes.size());
        assertEquals(0, superfluousAttributes.size());
        assertEquals(0, incorrectTextValues.size());
        assertEquals(0, incorrectAttributeValues.size());
        assertThat(result)
            .hasSize(2)
            .allMatch(node -> node.getPath().equals("//child") && node.getName().equals("child"));
    }

    @Test
    void compare_invalidSortingExpression() {
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
                    <child2 id="1"></child2>
                    <child2 id="2"></child2>
                </root>
            </xquery-result>
            """;
        var sorting = List.of("//child+2");

        // Act & Assert
        assertThrows(AnalysisException.class, () -> new AnalysisImpl(new XQResult(submission), new XQResult(solution), sorting));
    }

    @Test
    void validateSortingExpressions() {
        // Arrange
        var submission = """
            <xquery-result>
                <root>
                    <tmp id="1"></tmp>
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
                    <child2 id="1"></child2>
                    <child2 id="2"></child2>
                    <tmp id="1"></tmp>
                </root>
            </xquery-result>
            """;
        var sorting = List.of("//something");

        // Act & Assert
        assertThrows(AnalysisException.class, () -> new AnalysisImpl(new XQResult(submission), new XQResult(solution), sorting));
    }
}
