package lesson3.structure;

import java.util.ArrayList;
import java.util.List;

/**
 * 记录分析后的语法树
 * @param <T>
 */
public class Node<T> {
    private List<Node<T>> child;
    private T content;
    private int stipulationNum;

    public Node(T content) {
        child = new ArrayList<>();
        this.content = content;
        stipulationNum=0;
    }

    public void addChildNode(Node<T> n) {
        child.add(n);
    }

    public List<Node<T>> getChild() {
        return child;
    }


    public void setContent(T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }

}