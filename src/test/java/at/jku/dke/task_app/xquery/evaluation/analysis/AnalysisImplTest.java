package at.jku.dke.task_app.xquery.evaluation.analysis;

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
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), List.of());
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
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), List.of());
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
    void compare_displacedNodesWithValues3() throws AnalysisException {
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
                                           <ean>9-396-7510-9-00</ean>
                                           <vkPreis>13000</vkPreis>
                                           <preisRed>1000</preisRed>
                                           <bestand>15</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>5-2671-955-5-55</ean>
                                           <vkPreis>7000</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>12</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>3-1111-654-3-99</ean>
                                           <vkPreis>1700</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>7</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>6-231-4777-3-15</ean>
                                           <vkPreis>500</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>35</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>5-6661-000-0-00</ean>
                                           <vkPreis>450</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>11</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>0-4381-880-7-00</ean>
                                           <vkPreis>1250</vkPreis>
                                           <preisRed>250</preisRed>
                                           <bestand>85</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>4-1161-730-3-88</ean>
                                           <vkPreis>500</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>25</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>6-581-1766-3-45</ean>
                                           <vkPreis>200</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>40</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>1-4444-652-8-88</ean>
                                           <vkPreis>4500</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>4</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>7-2881-760-3-70</ean>
                                           <vkPreis>1500</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>23</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>2-446-7240-9-15</ean>
                                           <vkPreis>22500</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>7</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>1-256-7700-2-00</ean>
                                           <vkPreis>1999</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>30</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>0-55-48567-16-2</ean>
                                           <vkPreis>300</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>100</bestand>
                                       </prodInSortiment>
            </xquery-result>
            """;
        var solution = """
            <xquery-result>
                <prodInSortiment>
                                    <ean>2-446-7240-9-15</ean>
                                    <vkPreis>22500</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>7</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>9-396-7510-9-00</ean>
                                    <vkPreis>13000</vkPreis>
                                    <preisRed>1000</preisRed>
                                    <bestand>15</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>5-2671-955-5-55</ean>
                                    <vkPreis>7000</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>12</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>1-4444-652-8-88</ean>
                                    <vkPreis>4500</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>4</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>1-256-7700-2-00</ean>
                                    <vkPreis>1999</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>30</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>3-1111-654-3-99</ean>
                                    <vkPreis>1700</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>7</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>7-2881-760-3-70</ean>
                                    <vkPreis>1500</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>23</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>0-4381-880-7-00</ean>
                                    <vkPreis>1250</vkPreis>
                                    <preisRed>250</preisRed>
                                    <bestand>85</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>6-231-4777-3-15</ean>
                                    <vkPreis>500</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>35</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>4-1161-730-3-88</ean>
                                    <vkPreis>500</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>25</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>5-6661-000-0-00</ean>
                                    <vkPreis>450</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>11</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>1-626-7767-2-99</ean>
                                    <vkPreis>420</vkPreis>
                                    <preisRed>10</preisRed>
                                    <bestand>100</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>0-55-48567-16-2</ean>
                                    <vkPreis>300</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>100</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>0-456-4887-3-22</ean>
                                    <vkPreis>229</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>130</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>6-581-1766-3-45</ean>
                                    <vkPreis>200</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>40</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>0-777-4997-2-43</ean>
                                    <vkPreis>120</vkPreis>
                                    <preisRed>30</preisRed>
                                    <bestand>150</bestand>
                                </prodInSortiment>
            </xquery-result>
            """;
        var sorting = List.of("//vkPreis");

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
            .hasSize(14)
            .allMatch(node -> node.getPath().equals("//vkPreis"));
    }

    @Test
    void compare_displacedNodesWithValues3_withoutSorting() throws AnalysisException {
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
                                           <ean>9-396-7510-9-00</ean>
                                           <vkPreis>13000</vkPreis>
                                           <preisRed>1000</preisRed>
                                           <bestand>15</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>5-2671-955-5-55</ean>
                                           <vkPreis>7000</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>12</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>3-1111-654-3-99</ean>
                                           <vkPreis>1700</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>7</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>6-231-4777-3-15</ean>
                                           <vkPreis>500</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>35</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>5-6661-000-0-00</ean>
                                           <vkPreis>450</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>11</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>0-4381-880-7-00</ean>
                                           <vkPreis>1250</vkPreis>
                                           <preisRed>250</preisRed>
                                           <bestand>85</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>4-1161-730-3-88</ean>
                                           <vkPreis>500</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>25</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>6-581-1766-3-45</ean>
                                           <vkPreis>200</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>40</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>1-4444-652-8-88</ean>
                                           <vkPreis>4500</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>4</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>7-2881-760-3-70</ean>
                                           <vkPreis>1500</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>23</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>2-446-7240-9-15</ean>
                                           <vkPreis>22500</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>7</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>1-256-7700-2-00</ean>
                                           <vkPreis>1999</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>30</bestand>
                                       </prodInSortiment>
                       <prodInSortiment>
                                           <ean>0-55-48567-16-2</ean>
                                           <vkPreis>300</vkPreis>
                                           <preisRed>0</preisRed>
                                           <bestand>100</bestand>
                                       </prodInSortiment>
            </xquery-result>
            """;
        var solution = """
            <xquery-result>
                <prodInSortiment>
                                    <ean>2-446-7240-9-15</ean>
                                    <vkPreis>22500</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>7</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>9-396-7510-9-00</ean>
                                    <vkPreis>13000</vkPreis>
                                    <preisRed>1000</preisRed>
                                    <bestand>15</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>5-2671-955-5-55</ean>
                                    <vkPreis>7000</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>12</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>1-4444-652-8-88</ean>
                                    <vkPreis>4500</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>4</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>1-256-7700-2-00</ean>
                                    <vkPreis>1999</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>30</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>3-1111-654-3-99</ean>
                                    <vkPreis>1700</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>7</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>7-2881-760-3-70</ean>
                                    <vkPreis>1500</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>23</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>0-4381-880-7-00</ean>
                                    <vkPreis>1250</vkPreis>
                                    <preisRed>250</preisRed>
                                    <bestand>85</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>6-231-4777-3-15</ean>
                                    <vkPreis>500</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>35</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>4-1161-730-3-88</ean>
                                    <vkPreis>500</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>25</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>5-6661-000-0-00</ean>
                                    <vkPreis>450</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>11</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>1-626-7767-2-99</ean>
                                    <vkPreis>420</vkPreis>
                                    <preisRed>10</preisRed>
                                    <bestand>100</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>0-55-48567-16-2</ean>
                                    <vkPreis>300</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>100</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>0-456-4887-3-22</ean>
                                    <vkPreis>229</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>130</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>6-581-1766-3-45</ean>
                                    <vkPreis>200</vkPreis>
                                    <preisRed>0</preisRed>
                                    <bestand>40</bestand>
                                </prodInSortiment>
                <prodInSortiment>
                                    <ean>0-777-4997-2-43</ean>
                                    <vkPreis>120</vkPreis>
                                    <preisRed>30</preisRed>
                                    <bestand>150</bestand>
                                </prodInSortiment>
            </xquery-result>
            """;

        // Act
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), List.of());
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
            .anyMatch(node -> node.getPath().equals("//child") && node.getName().equals("root[1]/child[1]"))
            .anyMatch(node -> node.getPath().equals("//child") && node.getName().equals("root[1]/child[2]"));
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
        var analysis = new AnalysisImpl(new XQResult(submission), new XQResult(solution), List.of());
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
