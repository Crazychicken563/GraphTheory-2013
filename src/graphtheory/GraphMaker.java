package graphtheory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class GraphMaker {

    public static void main(String[] args) {
        try {
            FileWriter f = new FileWriter(new File("CustomMap.txt"));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        Scanner s = new Scanner(System.in);
        System.out.print("Num nodes: ");
        int numNodes = new Integer(s.nextLine()).intValue();
        System.out.println("Min edges per node: ");
        int minNodeEdges = new Integer(s.nextLine()).intValue();
        System.out.println("Max edges per node: ");
        int maxNodeEdges = new Integer(s.nextLine()).intValue();
        System.out.println("Min edge weight: ");
        int minWeight = new Integer(s.nextLine()).intValue();
        System.out.println("Max edge weight: ");
        int maxWeight = new Integer(s.nextLine()).intValue();
        ArrayList<Node> nodes = new ArrayList<>();
        Node temp;
        Random random = new Random();
        for (int i = 0; i < numNodes; i++) {
            temp = new Node(i + "", null);
            nodes.add(temp);
        }
        for (int i = 0; i < nodes.size(); i++) {
            temp = nodes.get(i);
            for (int r = 0; r < random.nextInt(maxNodeEdges - minNodeEdges) + minNodeEdges; r++) {
                int nextNode = random.nextInt(nodes.size());
                if (nextNode == i && nextNode != 0) {
                    nextNode--;
                } else if (nextNode == i) {
                    nextNode++;
                }
                temp.addEdge(new Edge(temp, nodes.get(nextNode), 5));
            }
        }
    }
}
