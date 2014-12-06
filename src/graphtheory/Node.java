package graphtheory;

import java.util.ArrayList;

public class Node {

    private ArrayList<Edge> edges;
    private String name;
    private Point location;
    private Node pred;
    private boolean visited;
    private boolean lockCoordx;
    private boolean lockCoordy;
    private ArrayList<Edge> lockEdges;
    private double[] movePriority;
    private boolean clicked;
    private double distFromStart;

    public Node(String name, ArrayList<Edge> edges) {
        if (edges != null) {
            this.edges = edges;
        } else {
            this.edges = new ArrayList<>();
        }
        this.name = name;
        location = new Point();
        pred = null;
        visited = false;
        lockCoordx = false;
        lockCoordy = false;
        lockEdges = new ArrayList<>();
        movePriority = new double[3];
        movePriority[2] = -1;
        distFromStart = Integer.MAX_VALUE;
    }

    public Node(Node node) {
        edges = node.getEdges();
        name = node.getName();
        location = node.getCoords();
        pred = node.getPred();
        visited = false;
        lockCoordx = false;
        lockCoordy = false;
        lockEdges = new ArrayList<>();
        movePriority = new double[3];
        movePriority[2] = -1;
        distFromStart = Integer.MAX_VALUE;
    }

    public void reset() {
        pred = null;
        visited = false;
        distFromStart = Integer.MAX_VALUE;
        clicked = false;
    }

    public void setCoords(Point coords) {
        Point temp = coords;
        if (lockCoordx) {
            temp.setX(this.location.getX());
        }
        if (lockCoordy) {
            temp.setY(this.location.getY());
        }
        this.location = new Point(temp);
    }

    public void setMovePriority(int x, int y, double priority) {
        if (priority > movePriority[2]) {
            movePriority[0] = x;
            movePriority[1] = y;
            movePriority[2] = priority;
        }
    }

    public double[] getMoves() {
        double[] temp = new double[3];
        temp[0] = movePriority[0];
        temp[1] = movePriority[1];
        temp[2] = movePriority[2];
        resetMoves();
        return temp;
    }

    public void resetMoves() {
        movePriority[0] = 0;
        movePriority[1] = 0;
        movePriority[2] = -1;
    }

    public void setCoords(double x, double y) {
        if (!lockCoordx) {
            location.setX(x);
        }
        if (!lockCoordy) {
            location.setY(y);
        }
    }

    public Point getCoords() {
        return location;
    }

    public boolean lockCoords(Edge e) {
        if (!lockEdges.contains(e)) {
            lockEdges.add(edges.get(edges.indexOf(e)));
        }
        if (lockEdges.size() == edges.size()) {
            lockCoordx = true;
            lockCoordy = true;
            return true;
        }
        return false;
    }

    public void unlockCoords(Edge e) {
        if (lockEdges.contains(e)) {
            lockEdges.remove(e);
        }
        lockCoordx = false;
        lockCoordy = false;
    }

    public void lockXCoord() {
        lockCoordx = true;
    }

    public void lockYCoord() {
        lockCoordy = true;
    }

    /*
     * returns true when repacing a previous pred
     */
    public boolean setPred(Node newPredecessor) {
        if (pred == null) {
            pred = newPredecessor;
            return false;
        }
        pred = newPredecessor;
        return true;
    }

    public Node getPred() {
        return pred;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public boolean addEdge(Edge edge) {
        if (!edges.contains(edge)) {
            edges.add(edge);
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public void visit() {
        visited = true;
    }

    public boolean wasVisited() {
        return visited;
    }

    public void click() {
        clicked = !clicked;
    }

    public boolean clicked() {
        return clicked;
    }

    public double dist() {
        return distFromStart;
    }

    public boolean relax(double newDist) {
        if (newDist < distFromStart) {
            distFromStart = newDist;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "\"" + name + "\"";
    }
}
