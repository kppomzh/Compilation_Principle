package oth.Util;

import com.zhzm.structure.GrammarTable;
import com.zhzm.structure.Node;

import java.util.ArrayList;
import java.util.List;

public class PrintGrammarObject {
    private static String indentation="    ",line="===>",T="┬",M="├",E="└";

    public static <T> void printTree(Node<T> node){
        List<StringBuilder> res=printNode(node);
        for (StringBuilder s:res){
            System.out.println(s.toString());
        }
    }

    private static <T> List<StringBuilder> printNode(Node<T> node){
        List<StringBuilder> res=new ArrayList<>();
        int length=node.getContent().toString().length();

        if(node.getChild().isEmpty()){
            res.add(new StringBuilder(node.getContent().toString()));
        }
        else {
            List<Node<T>> childs = node.getChild();
            if(childs.size()==1){
                List<StringBuilder> dres=printNode(childs.get(0));
                res.add(dres.get(0).insert(0,line).insert(0,'=').insert(0,node.getContent().toString()));
                for (int i = 1; i < dres.size() - 1; i++) {
                    res.add(dres.get(i).insert(0,getSpaces(length+5)));
                }
                if(dres.size()>1){
                    res.add(dres.get(dres.size() - 1).insert(0,getSpaces(length+5)));
                }
            }
            else {
                List<StringBuilder> dres=printNode(childs.get(0));
                res.add(dres.get(0).insert(0,line).insert(0,T).insert(0,node.getContent().toString()));
                for (int i = 1; i < dres.size() - 1; i++) {
                    res.add(dres.get(i).insert(0,getSpaces(4)).insert(0,M).insert(0,getSpaces(length)));
                }
                if(dres.size()>1){
                    res.add(dres.get(dres.size() - 1).insert(0,getSpaces(4)).insert(0,M).insert(0,getSpaces(length)));
                }

                for (int i = 1; i < node.getChild().size() - 1; i++) {
                    dres=printNode(childs.get(i));
                    res.add(dres.get(0).insert(0,line).insert(0,M).insert(0,getSpaces(length)));
                    for (int j = 1; j < dres.size() - 1; j++) {
                        res.add(dres.get(j).insert(0,getSpaces(4)).insert(0,M).insert(0,getSpaces(length)));
                    }
                    if(dres.size()>1){
                        res.add(dres.get(dres.size() - 1).insert(0,getSpaces(4)).insert(0,M).insert(0,getSpaces(length)));
                    }
                }

                dres=printNode(childs.get(node.getChild().size() - 1));
                res.add(dres.get(0).insert(0,line).insert(0,E).insert(0,getSpaces(length)));
                for (int i = 1; i < dres.size() - 1; i++) {
                    res.add(dres.get(i).insert(0,getSpaces(length+5)));
                }
                if(dres.size()>1){
                    res.add(dres.get(dres.size() - 1).insert(0,getSpaces(length+5)));
                }
            }
        }
        return res;
    }

    private static String getSpaces(int length){
        StringBuilder sb=new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    public static void printGrammarTable(GrammarTable table){
        List<Integer> longest=new ArrayList<>();

        int termax=0;
        for(String terminal:table.getTerminal()){
            termax= Math.max(termax, terminal.length());
        }
        longest.add(termax);

        for(String nonterminal:table.getNonterminal()){
            int max=nonterminal.length();
            for(String terminal:table.getTerminal()){
                List<String> prod=table.getProduct(nonterminal,terminal);
                if(prod==null) {
                    max=Math.max(max,6);
                    continue;
                }
                StringBuilder sb=new StringBuilder();
                for(String word:prod){
                    sb.append(word);
                    sb.append(' ');
                }
                if(sb.length()>1)
                    sb.deleteCharAt(sb.length()-1);
                max=Math.max(max,sb.length());
            }
            longest.add(max);
        }

        printSplitLine(longest);
        System.out.print(getPrefix(longest.get(0)));
        int loop=1;
        for(String nonterminal:table.getNonterminal()){
            System.out.print(getPrefix(longest.get(loop)-nonterminal.length()));
            System.out.print(nonterminal);
            loop++;
        }
        System.out.print('|');
        for(String terminal:table.getTerminal()){
            printSplitLine(longest);
            loop=0;
            System.out.print(getPrefix(longest.get(loop)-terminal.length()));
            System.out.print(terminal);
            for(String nonTerminal:table.getNonterminal()){
                loop++;

                List<String> prod=table.getProduct(nonTerminal,terminal);
                if(prod==null) {
                    System.out.print(getPrefix(longest.get(loop)-6));
                    System.out.print("[null]");
                    continue;
                }
                StringBuilder sb=new StringBuilder();
                for(String word:prod){
                    sb.append(word);
                    sb.append(' ');
                }
                if(sb.length()>1)
                    sb.deleteCharAt(sb.length()-1);
                System.out.print(getPrefix(longest.get(loop)-sb.length()));
                System.out.print(sb);
            }
            System.out.print('|');
        }
        printSplitLine(longest);
    }

    private static StringBuilder getPrefix(int length){
        StringBuilder sb=new StringBuilder();
        sb.append('|');
        for (int i = 0; i < length; i++) {
            sb.append(' ');
        }
        return sb;
    }

    private static void printSplitLine(List<Integer> longest){
        System.out.println();
        StringBuilder sb=new StringBuilder();
        sb.append('|');
        for(int length:longest){
            for (int i = 0; i < length; i++) {
                sb.append('-');
            }
            sb.append('|');
        }
        System.out.println(sb);
    }
}
