package at.jku.dke.task_app.xquery.evaluation.analysis;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.DifferenceEvaluator;

import java.util.ArrayList;
import java.util.List;

// Source: https://github.com/xmlunit/xmlunit/issues/123#issuecomment-401799502

/**
 * Performs brute force detection of children.
 */
public class BruteForceDifferenceEvaluator implements DifferenceEvaluator {
    /**
     * Creates a new instance of class {@link BruteForceDifferenceEvaluator}.
     */
    public BruteForceDifferenceEvaluator() {
    }

    /**
     * May alter the outcome of a comparison.
     *
     * @param comparison The comparison.
     * @param outcome    The current outcome of the comparison.
     * @return The new result of the comparison.
     */
    @Override
    public ComparisonResult evaluate(Comparison comparison, ComparisonResult outcome) {
        if (outcome == ComparisonResult.DIFFERENT
            && (comparison.getType() == ComparisonType.TEXT_VALUE || comparison.getType() == ComparisonType.ATTR_VALUE)) {
            outcome = ascendingBruteForceParentNodesComparison(comparison, outcome);
        }
        return outcome;
    }

    private static ComparisonResult ascendingBruteForceParentNodesComparison(Comparison comparison, ComparisonResult outcome) {
        List<Node> parentTestNodes = constructParentNodesHierarchy(comparison.getTestDetails().getTarget());
        Node controlNode = comparison.getControlDetails().getTarget();

        for (Node parentTestNode : parentTestNodes) {
            Node foundNode = findEqualNodeInHierarchy(controlNode, parentTestNode);

            if (foundNode != null && areNodesAtTheSameHierarchyLevel(controlNode, foundNode))
                return ComparisonResult.SIMILAR;
        }

        return outcome;
    }

    private static Node findEqualNodeInHierarchy(Node controlNode, Node testNode) {
        if (controlNode == null || testNode == null)
            return null;
        if (controlNode.isEqualNode(testNode))
            return testNode;

        NodeList children = testNode.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node childNode = children.item(i);
            Node foundNode = findEqualNodeInHierarchy(controlNode, childNode);
            if (foundNode != null)
                return foundNode;
        }

        return null;
    }

    private static boolean areNodesAtTheSameHierarchyLevel(Node controlNode, Node testNode) {
        List<Node> parentControlNodes = constructParentNodesHierarchy(controlNode);
        List<Node> parentTestNodes = constructParentNodesHierarchy(testNode);

        if (parentControlNodes.size() != parentTestNodes.size())
            return false;


        for (int i = 0; i < parentControlNodes.size(); ++i) {
            Node parentControlNode = parentControlNodes.get(i);
            Node parentTestNode = parentTestNodes.get(i);

            if (!parentControlNode.getNodeName().equals(parentTestNode.getNodeName()))
                return false;
        }

        return true;
    }

    private static List<Node> constructParentNodesHierarchy(Node node) {
        var parentNodes = new ArrayList<Node>();
        if (node == null || node.getParentNode() == null)
            return parentNodes;

        Node localParentNode = node.getParentNode();
        while (localParentNode != null) {
            parentNodes.add(localParentNode);
            localParentNode = localParentNode.getParentNode();
        }

        return parentNodes;
    }
}
