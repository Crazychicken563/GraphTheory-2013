package graphtheory;

public class Edge implements Comparable {

    private Node[] nodes;
    private double weight;

    public Edge(Node node1, Node node2, double weight) {
        nodes = new Node[2];
        nodes[0] = node1;
        nodes[1] = node2;
        this.weight = weight;
    }

    public Node[] getNode() {
        return nodes;
    }

    public double weight() {
        return weight;
    }

    @Override
    public String toString() {
        return "\"" + nodes[0].getName() + "\"-" + weight + "-\"" + nodes[1].getName() + "\"";
    }

    @Override
    public int compareTo(Object o) {
        if (((Edge) o).weight() < weight) {
            return 1;
        } else if (((Edge) o).weight() > weight) {
            return -1;
        } else {
            return 0;
        }
    }
}
