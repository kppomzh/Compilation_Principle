package lesson2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Node {
    private List<Node> child;
    private Word content;
    private int stipulationNum;

    public Node(Word content) {
        child = new ArrayList<>();
        this.content = content;
        stipulationNum=0;
    }

    public void addChildNode(Node n) {
        child.add(n);
    }

    public List<Node> getChild() {
        return child;
    }


    public void setContent(Word content) {
        this.content = content;
    }

    public Word getContent() {
        return content;
    }

}