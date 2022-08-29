package lesson3.test;

import lesson3.*;
import lesson3.Exception.AnalyzeException;
import lesson3.structure.Node;
import lesson3.structure.Word;

import java.io.IOException;
import java.util.LinkedList;

public class main {
    public static void main(String[]args) throws AnalyzeException, IOException {
        less();
    }

    public static void less() throws AnalyzeException, IOException {
        GrammarTable table= Analyze.analysisProcess("Caculator.grammar");
        PrintGrammarObject.printGrammarTable(table);

//        String expression="(3-5)/2";
//        String expression="sin(3-5)/2";
//        String expression="(3-5)/cos(2)";
        String expression="(3-5)/random()";
        FiniteAutoSegment segment=new FiniteAutoSegment();
        LinkedList<Word> words=segment.stateMachine(expression);

        Parser parser=new Parser(table);
        parser.setDebug(true);
        Node<Word> root=parser.start(words);
        PrintGrammarObject.printTree(root);
    }


}
