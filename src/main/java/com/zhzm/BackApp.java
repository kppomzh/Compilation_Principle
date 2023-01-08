package com.zhzm;

import com.zhzm.Exception.BuildException;
import com.zhzm.Exception.SemanticException;
import com.zhzm.analyzer.Parser;
import com.zhzm.analyzer.TreeBuilder;
import com.zhzm.analyzer.TreeVisitor;
import com.zhzm.grammarclass.ParserTreeNode;
import com.zhzm.structure.GrammarTable;
import com.zhzm.structure.Node;
import com.zhzm.structure.Word;
import oth.Util.FiniteAutoSegment;
import oth.Util.PrintGrammarObject;
import oth.Util.SerialUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

public class BackApp {
    public static void main( String[] args ) throws BuildException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException, SemanticException {
        String grammarFileName="CaculatorPlusAttr.grammar";
        GrammarTable table= (GrammarTable) new SerialUtil().readSerObj(grammarFileName);
//        PrintGrammarObject.printGrammarTable(table);

        String expression="(3-5+7*12)/-2";
        FiniteAutoSegment segment=new FiniteAutoSegment();
        LinkedList<Word> words=segment.stateMachine(expression);

        Parser parser=new Parser(table);
        parser.setDebug(true);
        Node<Word> root=parser.start(words);
//        PrintGrammarObject.printTree(root);

        TreeBuilder builder=new TreeBuilder(table,"com.zhzm.grammarclass");
        ParserTreeNode treeRoot= builder.start(words);
        System.out.println(treeRoot.getSubStance(null));
    }
}
