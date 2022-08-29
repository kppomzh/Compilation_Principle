package lesson3;

import lesson3.Exception.GrammarException;
import lesson3.structure.Node;
import lesson3.structure.Word;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Parser {
    private Stack<String> stack;
    private Node<Word> treeRoot;
    private GrammarTable gtable;
    private boolean debug=false;
    private Word end=new Word("#","#",-1,-1);

    public Parser(GrammarTable table){
        stack=new Stack<>();
        this.gtable=table;
    }

    public Node<Word> start(LinkedList<Word> wordSream){
        wordSream.add(end);

        stack.push("S");

        recursion(wordSream,makeNode("S",wordSream.getFirst()));

        return treeRoot.getChild().get(0);
    }

    private void recursion(LinkedList<Word> wordStream,Node<Word> upperNode){
        Word now=wordStream.getFirst();
        String lastStackItem=stack.pop();
        //限制该非终结符的分析不能越过临时栈底
        int tempStackButtom=stack.size();
        List<String> prodr=gtable.getProduct(lastStackItem,now.getName());
        if(prodr==null){
            //如果从gtable中查出的列表为null，说明这个非终结符无法按照当前的终结符进行分析，报错
            throw new GrammarException(now);
        }
        for (int i = prodr.size()-1; i>=0; i--) {
            stack.push(prodr.get(i));
        }
        Node<Word> node=makeNode(lastStackItem, now,upperNode);

        while(stack.size()>tempStackButtom){
            if(debug)
            {
                System.out.println("Stream:");
                for(Word str:wordStream){
                    System.out.print(str.getSubstance());
                    System.out.print(' ');
                }
                System.out.println();
                System.out.println("----------------");
            }

            if(gtable.getNonterminal().contains(stack.peek())){
                recursion(wordStream,node);
                now=wordStream.getFirst();
            }
            //栈顶终结符与输入终结符不匹配
            else if(!stack.peek().equals(now.getName())){
                throw new GrammarException(now);
            }
            else{
                //改写后要弹出栈顶和字符串头所有相同的终结符
                stack.pop();
                makeNode(now,node);
                wordStream.removeFirst();
                now=wordStream.getFirst();

                if(debug)
                {
                    System.out.println("Stack:");
                    for(String str:stack){
                        System.out.print(str);
                        System.out.print(' ');
                    }
                    System.out.println();
                    System.out.println("----------------");
                }
            }
        }
    }

    private void makeNode(Word terminal,Node<Word> upperNode){
        Node<Word> n=new Node<>(terminal);
        upperNode.addChildNode(n);
    }

    private Node<Word> makeNode(String nonterminal,Word terminal,Node<Word> upperNode){
        Word w=new Word("nonterminal",nonterminal, terminal.getLine(), terminal.getList());
        Node<Word> n=new Node<>(w);

        upperNode.addChildNode(n);
        return n;
    }

    private Node<Word> makeNode(String nonterminal,Word terminal){
        Word w=new Word("nonterminal",nonterminal, terminal.getLine(), terminal.getList());
        Node<Word> n=new Node<>(w);

        treeRoot=n;
        return n;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
