package at.jku.dke.task_app.xquery.evaluation.analysis;

import at.jku.dke.task_app.xquery.data.entities.XQueryTask;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
    void compare_invalidNames() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
                <root di="1" name="a">
                    <chlid></chlid>
                </root>
            </xquery-result>
            """;
        var solution = """
            <xquery-result>
                <root id="1" name="a">
                    <child></child>
                </root>
            </xquery-result>
            """;

        // Act
        var task = new XQueryTask();
        task.setSolutionAttributes(List.of("id", "name"));
        task.setSolutionElements(List.of("root", "child", "xquery-result"));
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), task);
        var elements = analysis.getInvalidElementNames();
        var attributes = analysis.getInvalidAttributeNames();

        // Assert
        assertThat(elements)
            .hasSize(1)
            .containsExactly("chlid");
        assertThat(attributes)
            .hasSize(1)
            .containsExactly("di");
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
            .anyMatch(node -> node.getPath().equals("/root[1]/child2[1]") && node.getName().equals("id") && node.getExpectedValue().equals("2"))
            .anyMatch(node -> node.getPath().equals("/root[1]/sub[1]/child[1]") && node.getName().equals("id") && node.getExpectedValue().equals("2"));
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
            .anyMatch(node -> node.getPath().equals("/root[1]/sub[1]/child[1]") && node.getExpectedValue().equals("2"));
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
    void compare_incorrectTextValueMissingNode() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
                <root>
                    <child>3</child>
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
        var result = analysis.getMissingNodes();
        var resultTextValues = analysis.getIncorrectTextValues();

        // Assert
        assertThat(resultTextValues)
            .isEmpty();
        assertThat(result)
            .hasSize(1)
            .anyMatch(node -> node.getPath().equals("/root[1]") && node.getName().equals("child"));
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
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), new XQueryTask(sorting));
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
            .anyMatch(node -> node.getPath().equals("//child2") && node.getName().equals("/root[1]/sub[1]/child2[1]"))
            .anyMatch(node -> node.getPath().equals("//child2") && node.getName().equals("/root[1]/sub[1]/child2[2]"));
    }

    @Test
    void compare_displacedNodesWithValues_withoutSorting() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
                <root>
                    <child>2</child>
                    <child>1</child>
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

        // Act
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), new XQueryTask(List.of()));
        var missingNodes = analysis.getMissingNodes();
        var superfluousNodes = analysis.getSuperfluousNodes();
        var incorrectTextValues = analysis.getIncorrectTextValues();
        var result = analysis.getDisplacedNodes();

        // Assert
        assertEquals(0, missingNodes.size());
        assertEquals(0, superfluousNodes.size());
        assertEquals(0, incorrectTextValues.size());
        assertThat(result).hasSize(0);
    }

    @Test
    void compare_displacedNodesWithValues2() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
                <root>
                    <child>2</child>
                    <child>1</child>
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
        var sorting = List.of("//child2");

        // Act
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), new XQueryTask(sorting));
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
            .anyMatch(node -> node.getPath().equals("//child2") && node.getName().equals("/root[1]/sub[1]/child2[1]"))
            .anyMatch(node -> node.getPath().equals("//child2") && node.getName().equals("/root[1]/sub[1]/child2[2]"));
    }

    @Test
    void compare_displacedNodesWithValues2_withoutSorting() throws AnalysisException {
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

        // Act
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), new XQueryTask(List.of()));
        var missingNodes = analysis.getMissingNodes();
        var superfluousNodes = analysis.getSuperfluousNodes();
        var incorrectTextValues = analysis.getIncorrectTextValues();
        var result = analysis.getDisplacedNodes();

        // Assert
        assertEquals(0, missingNodes.size());
        assertEquals(0, superfluousNodes.size());
        assertEquals(0, incorrectTextValues.size());
        assertThat(result).hasSize(0);
    }

    @Test
    void compare_displacedNodesWithValuesNested() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
               <prodInSortiment>
                   <ean>0-777-4997-2-43</ean>
                   <vkPreis>120</vkPreis>
                   <preisRed>30</preisRed>
                   <bestand>150</bestand>
               </prodInSortiment>
               <prodInSortiment>
                   <ean>9-396-7510-9-00</ean>
                   <vkPreis>13000</vkPreis>
                   <preisRed>1000</preisRed>
                   <bestand>15</bestand>
               </prodInSortiment>
               <prodInSortiment>
                   <vkPreis>229</vkPreis>
                   <preisRed>0</preisRed>
                   <bestand>130</bestand>
                   <ean>0-456-4887-3-22</ean>
               </prodInSortiment>
               <prodInSortiment>
                   <ean>1-626-7767-2-99</ean>
                   <bestand>100</bestand>
                   <vkPreis>420</vkPreis>
                   <preisRed>10</preisRed>
               </prodInSortiment>
    </xquery-result>""";
        var solution = """
            <xquery-result>
               <prodInSortiment>
                   <ean>0-456-4887-3-22</ean>
                   <vkPreis>229</vkPreis>
                   <preisRed>0</preisRed>
                   <bestand>130</bestand>
               </prodInSortiment>
               <prodInSortiment>
                   <ean>1-626-7767-2-99</ean>
                   <vkPreis>420</vkPreis>
                   <preisRed>10</preisRed>
                   <bestand>100</bestand>
               </prodInSortiment>
               <prodInSortiment>
                   <ean>0-777-4997-2-43</ean>
                   <vkPreis>120</vkPreis>
                   <preisRed>30</preisRed>
                   <bestand>150</bestand>
               </prodInSortiment>
               <prodInSortiment>
                   <ean>9-396-7510-9-00</ean>
                   <vkPreis>13000</vkPreis>
                   <preisRed>1000</preisRed>
                   <bestand>15</bestand>
               </prodInSortiment>
            </xquery-result>""";
        var sorting = List.of("//vkPreis");

        // Act
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), new XQueryTask(sorting));
        var missingNodes = analysis.getMissingNodes();
        var superfluousNodes = analysis.getSuperfluousNodes();
        var incorrectTextValues = analysis.getIncorrectTextValues();
        var result = analysis.getDisplacedNodes();

        // Assert
        assertThat(result)
            .hasSize(4)
            .allMatch(node -> node.getPath().equals("//vkPreis"));
        assertEquals(0, missingNodes.size());
        assertEquals(0, superfluousNodes.size());
        assertEquals(0, incorrectTextValues.size());
    }

    @Test
    void compare_displacedNodesWithValuesNested_withoutSorting() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
               <prodInSortiment>
                   <ean>0-777-4997-2-43</ean>
                   <vkPreis>120</vkPreis>
                   <preisRed>30</preisRed>
                   <bestand>150</bestand>
               </prodInSortiment>
               <prodInSortiment>
                   <ean>9-396-7510-9-00</ean>
                   <vkPreis>13000</vkPreis>
                   <preisRed>1000</preisRed>
                   <bestand>15</bestand>
               </prodInSortiment>
               <prodInSortiment>
                   <vkPreis>229</vkPreis>
                   <preisRed>0</preisRed>
                   <bestand>130</bestand>
                   <ean>0-456-4887-3-22</ean>
               </prodInSortiment>
               <prodInSortiment>
                   <ean>1-626-7767-2-99</ean>
                   <bestand>100</bestand>
                   <vkPreis>420</vkPreis>
                   <preisRed>10</preisRed>
               </prodInSortiment>
    </xquery-result>""";
        var solution = """
            <xquery-result>
               <prodInSortiment>
                   <ean>0-456-4887-3-22</ean>
                   <vkPreis>229</vkPreis>
                   <preisRed>0</preisRed>
                   <bestand>130</bestand>
               </prodInSortiment>
               <prodInSortiment>
                   <ean>1-626-7767-2-99</ean>
                   <vkPreis>420</vkPreis>
                   <preisRed>10</preisRed>
                   <bestand>100</bestand>
               </prodInSortiment>
               <prodInSortiment>
                   <ean>0-777-4997-2-43</ean>
                   <vkPreis>120</vkPreis>
                   <preisRed>30</preisRed>
                   <bestand>150</bestand>
               </prodInSortiment>
               <prodInSortiment>
                   <ean>9-396-7510-9-00</ean>
                   <vkPreis>13000</vkPreis>
                   <preisRed>1000</preisRed>
                   <bestand>15</bestand>
               </prodInSortiment>
            </xquery-result>""";

        // Act
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), new XQueryTask(List.of()));
        var missingNodes = analysis.getMissingNodes();
        var superfluousNodes = analysis.getSuperfluousNodes();
        var incorrectTextValues = analysis.getIncorrectTextValues();
        var result = analysis.getDisplacedNodes();

        // Assert
        assertThat(result).hasSize(0);
        assertEquals(0, missingNodes.size());
        assertEquals(0, superfluousNodes.size());
        assertEquals(0, incorrectTextValues.size());
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
                    <child2 id="2"></child2>
                    <child2 id="1"></child2>
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
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), new XQueryTask(sorting));
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
            .anyMatch(node -> node.getPath().equals("//child") && node.getName().equals("/root[1]/child[1]"))
            .anyMatch(node -> node.getPath().equals("//child") && node.getName().equals("/root[1]/child[2]"));
    }

    @Test
    void compare_displacedNodesWithAttributes_withoutSorting() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
                <root>
                    <tmp id="1"></tmp>
                    <child id="1"></child>
                    <child id="2"></child>
                    <child2 id="2"></child2>
                    <child2 id="1"></child2>
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

        // Act
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), new XQueryTask(List.of()));
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
        assertThat(result).hasSize(0);
    }

    @Test
    void compare_displacedNodesWithTextAndAttributes() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
                <root>
                    <tmp id="1">5</tmp>
                    <child id="1">2</child>
                    <child id="2">1</child>
                    <child2 id="2">4</child2>
                    <child2 id="1">3</child2>
                </root>
            </xquery-result>
            """;
        var solution = """
            <xquery-result>
                <root>
                    <child id="2">1</child>
                    <child id="1">2</child>
                    <child2 id="1">3</child2>
                    <child2 id="2">4</child2>
                    <tmp id="1">5</tmp>
                </root>
            </xquery-result>
            """;
        var sorting = List.of("//child");

        // Act
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), new XQueryTask(sorting));
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
            .anyMatch(node -> node.getPath().equals("//child") && node.getName().equals("/root[1]/child[1]"))
            .anyMatch(node -> node.getPath().equals("//child") && node.getName().equals("/root[1]/child[2]"));
    }

    @Test
    void compare_displacedNodesWithTextAndAttributes_withoutSorting() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
                <root>
                    <tmp id="1">5</tmp>
                    <child id="1">2</child>
                    <child id="2">1</child>
                    <child2 id="2">4</child2>
                    <child2 id="1">3</child2>
                </root>
            </xquery-result>
            """;
        var solution = """
            <xquery-result>
                <root>
                    <child id="2">1</child>
                    <child id="1">2</child>
                    <child2 id="1">3</child2>
                    <child2 id="2">4</child2>
                    <tmp id="1">5</tmp>
                </root>
            </xquery-result>
            """;

        // Act
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), new XQueryTask(List.of()));
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
            .hasSize(0);
    }

    @Test
    void compare_displacedNodesWithTextAndAttributes2() throws AnalysisException {
        // Arrange
        var submission = """
            <xquery-result>
                <root>
                    <tmp id="1">5</tmp>
                    <child id="1">2</child>
                    <child id="2">1</child>
                    <child2 id="2">4</child2>
                    <child2 id="1">7</child2>
                </root>
            </xquery-result>
            """;
        var solution = """
            <xquery-result>
                <root>
                    <child id="2">1</child>
                    <child id="1">2</child>
                    <child2 id="1">3</child2>
                    <child2 id="2">4</child2>
                    <tmp id="1">5</tmp>
                </root>
            </xquery-result>
            """;
        var sorting = List.of("//child");

        // Act
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), new XQueryTask(sorting));
        var missingAttributes = analysis.getMissingAttributes();
        var superfluousAttributes = analysis.getSuperfluousAttributes();
        var incorrectTextValues = analysis.getIncorrectTextValues();
        var incorrectAttributeValues = analysis.getIncorrectAttributeValues();
        var result = analysis.getDisplacedNodes();

        // Assert
        assertEquals(0, missingAttributes.size());
        assertEquals(0, superfluousAttributes.size());
        assertEquals(1, incorrectTextValues.size());
        assertEquals(0, incorrectAttributeValues.size());
        assertThat(result)
            .hasSize(2)
            .anyMatch(node -> node.getPath().equals("//child") && node.getName().equals("/root[1]/child[1]"))
            .anyMatch(node -> node.getPath().equals("//child") && node.getName().equals("/root[1]/child[2]"));
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
        assertThrows(AnalysisException.class, () -> new AnalysisImpl(new XQResult(submission), new XQResult(solution), new XQueryTask(sorting)));
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
        assertThrows(AnalysisException.class, () -> new AnalysisImpl(new XQResult(submission), new XQResult(solution), new XQueryTask(sorting)));
    }
}
