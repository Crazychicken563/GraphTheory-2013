package graphtheory;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

public class GraphTheory extends Applet implements Runnable, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    private Thread animation = null;
    private Graphics offScreen;
    private Image image;
    private ArrayList<Edge> edges;
    private ArrayList<Node> nodes;
    private double SCALE = 50;
    private boolean arrange = true;
    private boolean findPath = false;
    private boolean findPath2 = false;
    private static int WIDTH = 1550;
    private static int HEIGHT = 750;
    private ArrayList<Node> pathNodes = new ArrayList<>();
    private ArrayList<Edge> pathEdges = new ArrayList<>();
    private Point drawnCenter;
    private int preX, preY;
    private boolean pressOut = false;
    private Stack<Double> valuesOfStuff;
    //private boolean calculated = false;
    private double MSTweight = 0;
    private double pathWeight = 0;

    @Override
    public void init() {
        resize(WIDTH, HEIGHT);
        edges = new ArrayList<>();
        nodes = new ArrayList<>();
        image = createImage(WIDTH, HEIGHT);
        offScreen = image.getGraphics();
        addMouseListener(this);
        addKeyListener(this);
        addMouseMotionListener(this);
    }

    public void readIn(String fileName) {
        Scanner reader = null;
        try {
            reader = new Scanner(new File(fileName + ".txt"));
        } catch (FileNotFoundException ex) {
            System.err.println("File Not Found");
        }
        StringTokenizer st;
        String name1;
        String name2;
        double weight;
        Node node1;
        Node node2;
        Edge edge;
        while (reader.hasNext()) {
            st = new StringTokenizer(reader.nextLine(), ",");
            name1 = st.nextToken();
            name2 = st.nextToken();
            weight = new Double(st.nextToken()).doubleValue();
            node1 = new Node(name1, null);
            for (int i = 0; i < nodes.size(); i++) {
                if (node1.getName().equals(nodes.get(i).getName())) {
                    node1 = nodes.remove(i);
                    break;
                }
            }
            node2 = new Node(name2, null);
            for (int i = 0; i < nodes.size(); i++) {
                if (node2.getName().equals(nodes.get(i).getName())) {
                    node2 = nodes.remove(i);
                    break;
                }
            }
            edge = new Edge(node1, node2, weight);
            node1.addEdge(edge);
            node2.addEdge(edge);
            nodes.add(node1);
            nodes.add(node2);
            edges.add(edge);
        }
    }

    public void readInCoords(String fileName) {
        Scanner reader = null;
        try {
            reader = new Scanner(new File(fileName + ".txt"));
        } catch (FileNotFoundException ex) {
            System.err.println("File Not Found");
        }
        StringTokenizer st;
        String name;
        double xCoord;
        double yCoord;
        while (reader.hasNext()) {
            st = new StringTokenizer(reader.nextLine(), ",");
            name = st.nextToken();
            yCoord = Double.parseDouble(st.nextToken());
            xCoord = Double.parseDouble(st.nextToken());
            for (int i = 0; i < nodes.size(); i++) {
                if (nodes.get(i).getName().equals(name)) {
                    nodes.get(i).setCoords(-(xCoord * 28) + WIDTH, -(yCoord * 32) + HEIGHT);
                    arrange = false;//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    break;
                }
            }
        }
        double minX = Integer.MAX_VALUE;
        double minY = Integer.MAX_VALUE;
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getCoords().getX() < minX) {
                minX = nodes.get(i).getCoords().getX();
            }
            if (nodes.get(i).getCoords().getY() < minY) {
                minY = nodes.get(i).getCoords().getY();
            }
        }
        minX -= 10;
        minY -= 10;
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).setCoords(nodes.get(i).getCoords().getX() - minX, nodes.get(i).getCoords().getY() - minY);
        }
    }

    @Override
    public void start() {
        if (animation == null) {
            animation = new Thread(this, "AnimationThread");
            animation.start();
        }
    }

    public double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public double distance(Point first, Point second) {
        return Math.sqrt(Math.pow(first.getX() - second.getX(), 2) + Math.pow(first.getY() - second.getY(), 2));
    }

    @Override
    public void run() {
        readIn("graph9");//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //System.out.println(nodes);
        //System.out.println(edges);
        if (true) {
            for (int i = 0; i < nodes.size() / 2; i++) {
                nodes.get(i).setCoords((WIDTH - 50) / nodes.size() * 2 * (i + 1) + 20, 100);
            }
            for (int i = nodes.size() / 2; i < nodes.size(); i++) {
                nodes.get(i).setCoords((WIDTH - 50) / nodes.size() * 2 * (i - nodes.size() / 2) + 20, 600);
            }
        } else {
            Random r = new Random();
            for (int i = 0; i < nodes.size(); i++) {
                nodes.get(i).setCoords(r.nextInt(WIDTH), r.nextInt(HEIGHT));
            }
        }
        //[0] = move x
        //[1] = move y
        //[2] = move priority;
        //System.out.println(averageEdgeLength(edges));
        SCALE = HEIGHT / averageEdgeLength(edges);
        readInCoords("us-nodes");
        //System.out.println(SCALE);
        double prevDerp = Integer.MAX_VALUE;
        Edge currEdge;
        int dx1;
        int dy1;
        int dx2;
        int dy2;
        Node first;
        Node second;
        drawnCenter = findCenter(nodes);
        while (arrange) {
            exPanda(nodes);
            for (int i = 0; i < edges.size(); i++) {
                currEdge = edges.get(i);
                first = currEdge.getNode()[0];
                second = currEdge.getNode()[1];
                double priority = Math.abs(distance(first.getCoords(), second.getCoords()) - currEdge.weight() * SCALE);
                double distance = distance(first.getCoords(), second.getCoords());
                if (distance > currEdge.weight() * SCALE) {
                    if (first.getCoords().getX() >= second.getCoords().getX()) {
                        dx1 = 1;
                        dx2 = -1;
                        //dx1 = (int)(distance / SCALE);
                        //dx2 = -(int)(distance / SCALE);
                    } else {
                        dx1 = -1;
                        dx2 = 1;
                        //dx1 = -(int)(distance / SCALE);
                        //dx2 = (int)(distance / SCALE);
                    }
                    if (first.getCoords().getY() > second.getCoords().getY()) {
                        dy1 = 1;
                        dy2 = -1;
                        //dy1 = (int)(distance / SCALE);
                        //dy2 = -(int)(distance / SCALE);
                    } else {
                        dy1 = -1;
                        dy2 = 1;
                        //dy1 = -(int)(distance / SCALE);
                        //dy2 = (int)(distance / SCALE);
                    }
                } else if (distance < currEdge.weight() * SCALE) {
                    if (first.getCoords().getX() > second.getCoords().getX()) {
                        dx1 = -1;
                        dx2 = 1;
                        //dx1 = -(int)(distance / SCALE);
                        //dx2 = (int)(distance / SCALE);
                    } else {
                        dx1 = 1;
                        dx2 = -1;
                        //dx1 = (int)(distance / SCALE);
                        //dx2 = -(int)(distance / SCALE);
                    }
                    if (first.getCoords().getY() > second.getCoords().getY()) {
                        dy1 = -1;
                        dy2 = 1;
                        //dy1 = -(int)(distance / SCALE);
                        //dy2 = (int)(distance / SCALE);
                    } else {
                        dy1 = 1;
                        dy2 = -1;
                        //dy1 = (int)(distance / SCALE);
                        //dy2 = -(int)(distance / SCALE);
                    }
                } else {
                    dx1 = 0;
                    dx2 = 0;
                    dy1 = 0;
                    dy2 = 0;
                    //first.lockCoords(currEdge);
                    //second.lockCoords(currEdge);
                }
                first.setMovePriority(dx2, dy2, priority);
                second.setMovePriority(dx1, dy1, priority);
            }
            //System.out.println(graphDerp(edges) - prevDerp);
            double derpy = graphDerp(edges);
            if (Math.abs(derpy - prevDerp) < 5) {///////////////////////////////////////////////////////////////////////////////////////////////////////////
                for (int i = 0; i < nodes.size(); i++) {
                    Node curr = nodes.get(i);
                    for (int j = 0; j < edges.size(); j++) {
                        if (!curr.getEdges().contains(edges.get(j))) {
                            Point edge = edgeNodeIntersect(curr, edges.get(j));
                            if (edge.getX() != -1 && edge.getY() != -1) {
                                Point node = curr.getCoords();
                                double priority = SCALE / distance(node.getX(), node.getY(), edge.getX(), edge.getY()); //40
                                if (node.getX() > edge.getX()) {
                                    if (node.getY() > edge.getY()) {
                                        curr.setMovePriority(1, 1, priority);
                                    } else {
                                        curr.setMovePriority(1, -1, priority);
                                    }
                                } else {
                                    if (node.getY() > edge.getY()) {
                                        curr.setMovePriority(-1, 1, priority);
                                    } else {
                                        curr.setMovePriority(-1, -1, priority);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (Math.abs(derpy - prevDerp) < 5) {
                for (int i = 0; i < nodes.size(); i++) {
                    Node curr = nodes.get(i);
                    for (int j = 0; j < nodes.size(); j++) {
                        double priority = nodeIntersect(nodes.get(i), nodes.get(j));
                        if (i != j && priority != -1) {
                            priority = (SCALE * 12) / priority;//////////////////////////////////////////////////////////////////////////////////////////////////////////
                            //priority = 30000 / priority;
                            Point node = curr.getCoords();
                            Node node2 = nodes.get(j);
                            Point comp = node2.getCoords();
                            if (node.getX() > comp.getX()) {
                                if (node.getY() > comp.getY()) {
                                    curr.setMovePriority(1, 1, priority);
                                    node2.setMovePriority(-1, -1, priority);
                                } else {
                                    curr.setMovePriority(1, -1, priority);
                                    node2.setMovePriority(-1, 1, priority);
                                }
                            } else {
                                if (node.getY() > comp.getY()) {
                                    curr.setMovePriority(-1, 1, priority);
                                    node2.setMovePriority(1, -1, priority);
                                } else {
                                    curr.setMovePriority(-1, -1, priority);
                                    node2.setMovePriority(1, 1, priority);
                                }
                            }
                        }
                    }
                }
            }
            Point center = findCenter(nodes);
            dx1 = -1;
            dy1 = -1;
            if (center.getX() < (WIDTH / 2)) {
                dx1 = 1;
            }
            if (center.getY() < (HEIGHT / 2)) {
                dy1 = 1;
            }
            for (int i = 0; i < nodes.size(); i++) {
                Node temp = nodes.get(i);
                temp.setCoords(temp.getCoords().getIntX() + dx1, temp.getCoords().getIntY() + dy1);
                if (temp.getCoords().getIntX() < 0) {
                    temp.setCoords(temp.getCoords().getIntX() + 1, temp.getCoords().getIntY());
                }
                if (temp.getCoords().getIntX() > (WIDTH - 20)) {
                    temp.setCoords(temp.getCoords().getIntX() - 1, temp.getCoords().getIntY());
                }
                if (temp.getCoords().getIntY() < 0) {
                    temp.setCoords(temp.getCoords().getIntX(), temp.getCoords().getIntY() + 1);
                }
                if (temp.getCoords().getIntY() > (HEIGHT - 20)) {
                    temp.setCoords(temp.getCoords().getIntX(), temp.getCoords().getIntY() - 1);
                }
            }
            prevDerp = graphDerp(edges);
            for (int i = 0; i < nodes.size(); i++) {
                Node temp = nodes.get(i);
                double[] tempMove = temp.getMoves();
                //System.out.print(tempMove[2]+",");
                temp.setCoords(temp.getCoords().getIntX() + tempMove[0], temp.getCoords().getIntY() + tempMove[1]);
                //temp.resetMoves();
                if (temp.getCoords().getIntX() < 0) {
                    temp.setCoords(temp.getCoords().getIntX() + 1, temp.getCoords().getIntY());
                    //temp.lockXCoord();
                }
                if (temp.getCoords().getIntX() > (WIDTH - 20)) {
                    temp.setCoords(temp.getCoords().getIntX() - 1, temp.getCoords().getIntY());
                    //temp.lockXCoord();
                }
                if (temp.getCoords().getIntY() < 0) {
                    temp.setCoords(temp.getCoords().getIntX(), temp.getCoords().getIntY() + 1);
                    //temp.lockYCoord();
                }
                if (temp.getCoords().getIntY() > (HEIGHT - 20)) {
                    temp.setCoords(temp.getCoords().getIntX(), temp.getCoords().getIntY() - 1);
                }
                //nodes.set(i, temp);
            }
            repaint();
            //while (true) {}
            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }
        }
        while (!arrange) {
            //if (!calculated) {
            pathNodes = new ArrayList<>();
            if (findPath) {
                for (int i = 0; i < nodes.size(); i++) {
                    if (nodes.get(i).clicked()) {
                        pathNodes.add(nodes.get(i));
                    }
                }
                if (pathNodes.size() == 2) {
                    pathNodes.get(0).relax(0);
                    Node curr;
                    for (int p = 0; p < nodes.size() - 1; p++) {
                        Set<Node> visited = new HashSet<>();
                        int pos = 0;
                        while (visited.size() < nodes.size()) {
                            curr = nodes.get(pos);
                            Node next = null;
                            for (int i = 0; i < curr.getEdges().size(); i++) {
                                Edge connector = curr.getEdges().get(i);
                                if (connector.getNode()[0].equals(curr)) {
                                    next = curr.getEdges().get(i).getNode()[1];
                                } else {
                                    next = curr.getEdges().get(i).getNode()[0];
                                }
                                if (next.relax(connector.weight() + curr.dist())) {
                                    next.setPred(curr);
                                }
                                visited.add(curr);
                            }
                            pos++;
                        }
                    }
                    curr = pathNodes.get(1);
                    pathWeight = curr.dist();
                    while (!pathNodes.get(0).equals(curr)) {
                        pathNodes.add(curr.getPred());
                        curr = curr.getPred();
                    }
                    //calculated = true;
                } else { //MINIMUMSPANNINGTERREERR
                    ArrayList<Node> mst = new ArrayList<>();
                    pathEdges = new ArrayList<>();
                    Node curr = nodes.get(0);
                    mst.add(curr);
                    Node next;
                    while (mst.size() < nodes.size()) {
                        PriorityQueue<Edge> currEdges = new PriorityQueue<>();
                        ArrayList<Edge> tempEdges;
                        for (int i = 0; i < mst.size(); i++) {
                            tempEdges = mst.get(i).getEdges();
                            for (int j = 0; j < tempEdges.size(); j++) {
                                if (!pathEdges.contains(tempEdges.get(j))) {
                                    if (!mst.contains(tempEdges.get(j).getNode()[0]) || !mst.contains(tempEdges.get(j).getNode()[1])) {
                                        currEdges.add(tempEdges.get(j));
                                    }
                                }
                            }
                        }
                        Edge connector = currEdges.poll();
                        if (connector != null) {
                            if (connector.getNode()[0].equals(curr)) {
                                curr = connector.getNode()[0];
                                next = connector.getNode()[1];
                            } else {
                                next = connector.getNode()[0];
                                curr = connector.getNode()[1];
                            }
                            pathEdges.add(connector);
                            if (!mst.contains(next)) {
                                mst.add(next);
                            }
                            if (!mst.contains(curr)) {
                                mst.add(curr);
                            }
                        }
                    }
                    //calculated = true;
                }
                MSTweight = 0;
                for (int i = 0; i < pathEdges.size(); i++) {
                    MSTweight += pathEdges.get(i).weight();
                }
                //System.out.println(MSTweight);////////////////////////////////////////////////////////////////////////////////////////////////////////
                //}
                //calculated = true;
            }
            repaint();
            try {
                Thread.sleep(50);
            } catch (Exception e) {
            }
        }
    }

    public Node Center(ArrayList<Node> ns, ArrayList<Edge> edges) {
        Node center = null;
        ArrayList<Node> nodes = new ArrayList<>();
        for (int i = 0; i < edges.size(); i++) {
            break;
        }
        return center;
    }

    public int numEdgeIntersects(ArrayList<Edge> edges) {
        int num = 0;
        for (int i = 0; i < edges.size(); i++) {
            for (int j = 0; j < edges.size(); j++) {
                if (i != j) {
                }
            }
        }
        return num;
    }

    public ArrayList<Node> findConnectedTo(ArrayList<Edge> edges, Node node) {
        ArrayList nodes = new ArrayList<>();
        for (int i = 0; i < edges.size(); i++) {
            if (edges.get(i).getNode()[0].equals(node)) {
                nodes.add(edges.get(i).getNode()[1]);
            } else {
                nodes.add(edges.get(i).getNode()[0]);
            }
        }
        return nodes;
    }

    public double graphDerp(ArrayList<Edge> edges) {
        double totalPimp = 0;
        for (int i = 0; i < edges.size(); i++) {
            totalPimp += Math.abs((edges.get(i).weight() * SCALE) - distance(edges.get(i).getNode()[0].getCoords().getX(),
                    edges.get(i).getNode()[0].getCoords().getY(),
                    edges.get(i).getNode()[1].getCoords().getX(),
                    edges.get(i).getNode()[1].getCoords().getY()));
        }
        return totalPimp;
    }

    public Point edgeEdgeIntersect(Edge edge1, Edge edge2) {
        Point node1 = edge1.getNode()[0].getCoords();
        Point node2 = edge1.getNode()[1].getCoords();
        Point node3 = edge2.getNode()[0].getCoords();
        Point node4 = edge2.getNode()[1].getCoords();
        Point intersect = new Point();
        double slope1;
        if (node1.getX() - node2.getX() != 0) {
            slope1 = (node1.getY() - node2.getY()) / (node1.getX() - node2.getX());
        } else {
            slope1 = 10;
        }
        double yInt1 = node1.getY() - (node1.getX() * slope1);
        double slope2;
        if (node3.getX() - node4.getX() != 0) {
            slope2 = (node3.getY() - node4.getY()) / (node4.getX() - node3.getX());
        } else {
            slope2 = 10;
        }
        double yInt2 = node3.getY() - (node3.getX() * slope2);

        return intersect;
    }

    public Point edgeNodeIntersect(Node n, Edge e) {
        Point node1 = e.getNode()[0].getCoords();
        Point node2 = e.getNode()[1].getCoords();
        Point node3 = n.getCoords();

        double slope1;
        if (node1.getX() - node2.getX() != 0) {
            slope1 = (node1.getY() - node2.getY()) / (node1.getX() - node2.getX());
        } else {
            slope1 = 10;
        }
        double yInt1 = node1.getY() - (node1.getX() * slope1);
        double slope2;
        if (slope1 == 0) {
            //slope2 = Integer.MAX_VALUE;
            slope1 = 0.01;
        }
        slope2 = -1 / slope1;
        double yInt2 = node3.getY() - (node3.getX() * slope2);
        double xInt = (yInt2 - yInt1) / (slope1 - slope2);
        double yInt = (slope1 * xInt + yInt1);

        if (distance(node3.getX(), node3.getY(), xInt, yInt) < 10) {
            return new Point(xInt, yInt);
        }
        //System.out.println("does this ever return false?");
        return new Point(-1, -1);
    }

    public Point findCenter(ArrayList<Node> nodes) {
        double xTotal = 0;
        double yTotal = 0;
        for (int i = 0; i < nodes.size(); i++) {
            xTotal += nodes.get(i).getCoords().getX();
            yTotal += nodes.get(i).getCoords().getY();
        }
        return new Point(xTotal / nodes.size(), yTotal / nodes.size());
    }

    public void exPanda(ArrayList<Node> nodes) {
        double maxX = Integer.MIN_VALUE;
        double minX = Integer.MAX_VALUE;
        double maxY = Integer.MIN_VALUE;
        double minY = Integer.MAX_VALUE;
        Point curr;
        for (int i = 0; i < nodes.size(); i++) {
            curr = nodes.get(i).getCoords();
            if (curr.getX() < minX) {
                minX = curr.getX();
            }
            if (curr.getX() > maxX) {
                maxX = curr.getX();
            }
            if (curr.getY() < minY) {
                minY = curr.getY();
            }
            if (curr.getY() > maxY) {
                maxY = curr.getY();
            }
        }
        double scaleUp = 0.05;
        double scaleDown = 0.05;
        if (minX > 100) {
            SCALE += scaleUp;
        }
        if (maxX < (WIDTH - 100)) {
            SCALE += scaleUp;
        }
        if (minY > 100) {
            SCALE += scaleUp;
        }
        if (maxY < (HEIGHT - 100)) {
            SCALE += scaleUp;
        }
        if (minX < 100) {
            SCALE -= scaleDown;
        }
        if (maxX > (WIDTH - 100)) {
            SCALE -= scaleDown;
        }
        if (minY < 100) {
            SCALE -= scaleDown;
        }
        if (maxY > (HEIGHT - 100)) {
            SCALE -= scaleDown;
        }
        if (SCALE < 0) {
            SCALE = 0.01;
        }
    }

    public double averageEdgeLength(ArrayList<Edge> edges) {
        double average = 0;
        for (int i = 0; i < edges.size(); i++) {
            average += edges.get(i).weight();
        }
        return average / nodes.size();
    }

    public double nodeIntersect(Node one, Node two) {
        double distance = distance(one.getCoords().getX(), one.getCoords().getY(), two.getCoords().getX(), two.getCoords().getY());
        if (distance < 20) {
            return distance;
        }
        return -1;
    }

    @Override
    public void paint(Graphics g) {
        offScreen.setColor(Color.white);
        offScreen.fillRect(0, 0, WIDTH, HEIGHT);
        offScreen.setColor(Color.black);
        offScreen.drawRect(0, 0, WIDTH, HEIGHT);
        if (arrange) {
            offScreen.drawString("Deviation: " + graphDerp(edges) + "pxls", 0, 10);
        } else {
            if (findPath) {
                if ((!pathNodes.isEmpty())) {
                    offScreen.drawString("Path Weight: " + pathWeight, 0, 10);
                } else {
                    offScreen.drawString("MST Weight: " + MSTweight, 0, 10);
                }
            }
        }
        if (!findPath) {
            for (int i = 0; i < edges.size(); i++) {
                offScreen.setColor(Color.black);
                int x1 = edges.get(i).getNode()[0].getCoords().getIntX() + 10;
                int y1 = edges.get(i).getNode()[0].getCoords().getIntY() + 10;
                int x2 = edges.get(i).getNode()[1].getCoords().getIntX() + 10;
                int y2 = edges.get(i).getNode()[1].getCoords().getIntY() + 10;
                offScreen.drawLine(x1, y1, x2, y2);
                offScreen.setColor(Color.red);
                offScreen.drawString(edges.get(i).weight() + "", (x1 + x2) / 2, (y1 + y2) / 2 + 5);
            }
        } else {
            for (int i = 0; i < edges.size(); i++) {
                offScreen.setColor(Color.decode("#E6E6E6"));
                int x1 = edges.get(i).getNode()[0].getCoords().getIntX() + 10;
                int y1 = edges.get(i).getNode()[0].getCoords().getIntY() + 10;
                int x2 = edges.get(i).getNode()[1].getCoords().getIntX() + 10;
                int y2 = edges.get(i).getNode()[1].getCoords().getIntY() + 10;
                offScreen.drawLine(x1, y1, x2, y2);
            }
            for (int i = 0; i < edges.size(); i++) {
                if (pathNodes.contains(edges.get(i).getNode()[0]) && pathNodes.contains(edges.get(i).getNode()[1])) {
                    offScreen.setColor(Color.black);
                    int x1 = edges.get(i).getNode()[0].getCoords().getIntX() + 10;
                    int y1 = edges.get(i).getNode()[0].getCoords().getIntY() + 10;
                    int x2 = edges.get(i).getNode()[1].getCoords().getIntX() + 10;
                    int y2 = edges.get(i).getNode()[1].getCoords().getIntY() + 10;
                    offScreen.drawLine(x1, y1, x2, y2);
                    offScreen.setColor(Color.red);
                    offScreen.drawString(edges.get(i).weight() + "", (x1 + x2) / 2, (y1 + y2) / 2 + 5);
                }
            }
            for (int i = 0; i < pathEdges.size(); i++) {
                offScreen.setColor(Color.black);
                int x1 = pathEdges.get(i).getNode()[0].getCoords().getIntX() + 10;
                int y1 = pathEdges.get(i).getNode()[0].getCoords().getIntY() + 10;
                int x2 = pathEdges.get(i).getNode()[1].getCoords().getIntX() + 10;
                int y2 = pathEdges.get(i).getNode()[1].getCoords().getIntY() + 10;
                offScreen.drawLine(x1, y1, x2, y2);
                offScreen.setColor(Color.red);
                offScreen.drawString(pathEdges.get(i).weight() + "", (x1 + x2) / 2, (y1 + y2) / 2 + 5);
            }
        }
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).clicked()) {
                offScreen.setColor(Color.yellow);
            } else {
                offScreen.setColor(Color.black);
            }
            offScreen.fillOval(nodes.get(i).getCoords().getIntX(), nodes.get(i).getCoords().getIntY(), 20, 20);
            offScreen.setColor(Color.red);
            offScreen.drawString(nodes.get(i).getName(), nodes.get(i).getCoords().getIntX() + 5, nodes.get(i).getCoords().getIntY() + 15);
        }
        g.drawImage(image, 0, 0, this);
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void stop() {
        animation = null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!arrange) {
            if (!findPath) {
                Point p;
                for (int i = 0; i < nodes.size(); i++) {
                    p = nodes.get(i).getCoords();
                    if (distance(e.getX() - 10, e.getY() - 10, p.getX(), p.getY()) < 10) {
                        nodes.get(i).click();
                        //System.out.println("clicked");
                    }
                }
            }
            preX = drawnCenter.getIntX() - e.getX();
            preY = drawnCenter.getIntY() - e.getY();
            updateLocation(e);
        }
    }

    public void updateLocation(MouseEvent e) {
        if (!arrange) {
            drawnCenter.setLocation(preX + e.getX(), preY + e.getY());
            Point center = findCenter(nodes);
            int dx = drawnCenter.getIntX() - center.getIntX();
            int dy = drawnCenter.getIntY() - center.getIntY();
            for (int i = 0; i < nodes.size(); i++) {
                nodes.get(i).setCoords(nodes.get(i).getCoords().getX() + dx, nodes.get(i).getCoords().getY() + dy);
            }
        }
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!arrange) {
            updateLocation(e);
            pressOut = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == ' ') {
            arrange = false;
            drawnCenter = findCenter(nodes);
        }
        if (e.getKeyChar() == 's') {
            if (arrange == false) {
                //System.out.println("GOGOGO");
                findPath = !findPath;
            }
            if (findPath == false) {
                for (int i = 0; i < nodes.size(); i++) {
                    nodes.get(i).reset();
                }
                pathEdges = new ArrayList<>();
                //calculated = false;
            }
        }
        if (e.getKeyChar() == 'w') {
            if (arrange == false) {
                //System.out.println("GOGOGO");
                findPath2 = !findPath2;
            }
        }
        if (!arrange) {
            boolean zoomed = false;
            double zoomScale = 1;
            if (e.getKeyChar() == '+') {
                zoomed = true;
                zoomScale = 1.05;
            }
            if (e.getKeyChar() == '-') {
                zoomed = true;
                zoomScale = 0.95;
            }
            if (zoomed) {
                for (int i = 0; i < nodes.size(); i++) {
                    nodes.get(i).setCoords(nodes.get(i).getCoords().getX() * zoomScale, nodes.get(i).getCoords().getY() * zoomScale);
                }
                Point center = findCenter(nodes);
                int dx = drawnCenter.getIntX() - center.getIntX();
                int dy = drawnCenter.getIntY() - center.getIntY();
                for (int i = 0; i < nodes.size(); i++) {
                    nodes.get(i).setCoords(nodes.get(i).getCoords().getX() + dx, nodes.get(i).getCoords().getY() + dy);
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!pressOut && !arrange) {
            updateLocation(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //drawnCenter = new Point(e.getX(), e.getY());
    }
}
