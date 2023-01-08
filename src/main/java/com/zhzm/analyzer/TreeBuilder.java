package com.zhzm.analyzer;

import com.zhzm.Exception.GrammarException;
import com.zhzm.grammarclass.ParserTreeNode;
import com.zhzm.structure.GrammarTable;
import com.zhzm.structure.Word;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class TreeBuilder {

    private Stack<String> stack;
    private GrammarTable gtable;
    private Word end=new Word("#","#",-1,-1);

    private String baseClassPath;

    public TreeBuilder(GrammarTable table,String baseClassPath){
        stack=new Stack<>();
        this.gtable=table;
        this.baseClassPath=baseClassPath;
    }

    public ParserTreeNode start(LinkedList<Word> wordSream) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        wordSream.add(end);

        stack.push("S");

        return recursion(wordSream);
    }

    private ParserTreeNode recursion(LinkedList<Word> wordStream) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, InstantiationException {
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
        ParserTreeNode node=makeNode(lastStackItem);
        if(prodr.isEmpty())
            node.isEmpty();

        while(stack.size()>tempStackButtom){
            if(gtable.getNonterminal().contains(stack.peek())){
                node.getSetMethod(stack.peek(),stack.peek()).invoke(node,recursion(wordStream));
                now=wordStream.getFirst();
            }
            //栈顶终结符与输入终结符不匹配
            else if(!stack.peek().equals(now.getName())){
                throw new GrammarException(now);
            }
            else{
                //改写后要弹出栈顶和字符串头所有相同的终结符
                stack.pop();
                node.getSetMethod(now.getName()).invoke(node,now.getSubstance());

                wordStream.removeFirst();
                now=wordStream.getFirst();
            }
        }

        return node;
    }

    private ParserTreeNode makeNode(String nonterminal) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ParserTreeNode n= (ParserTreeNode) Class.forName(baseClassPath+'.'+nonterminal).getDeclaredConstructor().newInstance();

        return n;
    }
}
