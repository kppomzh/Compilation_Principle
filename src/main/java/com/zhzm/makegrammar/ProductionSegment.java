package com.zhzm.makegrammar;

import com.zhzm.Exception.BuildException;
import com.zhzm.structure.Word;

import java.util.*;

public class ProductionSegment {
    private Set<String> keyWords;
    private Map<String,String> markWords;
    private int index,line,list;
    private StringBuilder sb;
    private ArrayList<Word> symbolList;
    private String toTrans;

    public ProductionSegment(){
        keyWords = new HashSet<>();
        markWords = new HashMap<>();
        setMarkWords();
    }

    private void init(String str){
        symbolList = new ArrayList<>();
        sb=new StringBuilder();
        toTrans=str;
        index =0;
    }

    public List<Word> stateMachine(String str) throws BuildException {
        init(str);
        while (index <str.length())
            node_0();
        return symbolList;
    }

    private void node_0() throws BuildException {
        for(; index <toTrans.length(); index++){
            switch (getCharState(toTrans.charAt(index))){
                case 1:
                    sb.append(toTrans.charAt(index));
                    index++;
                    node_1();
                    return;
//                case 2:
//                    sb.append(toTrans.charAt(index));
//                    index++;
//                    node_2();
//                    return;
                case 3:case 4:
                    sb.append(toTrans.charAt(index));
                    index++;
                    node_3();
                    return;
                case -1:
                    throw new BuildException();
                default:
                    break;
            }
        }
    }
    private void node_1() throws BuildException {
        upper:for(; index <toTrans.length(); index++) {
            switch (getCharState(toTrans.charAt(index))){
                case 1: case 2:
                    sb.append(toTrans.charAt(index));
                    break;
                case -1:
                    throw new BuildException();
                default:
                    break upper;
            }
        }
//        if(keyWords.contains(sb.toString()))
//            node_end("unterminal");
//        else
//            node_end("terminal");
        node_end(sb.toString());
    }
    private void node_2() throws BuildException {
        upper:for(; index <toTrans.length(); index++) {
            switch (getCharState(toTrans.charAt(index))){
                case 4:
                    sb.append(toTrans.charAt(index));
                    index++;
                    node_2_1();
                    return;
                case 2:
                    sb.append(toTrans.charAt(index));
                    break;
                case -1:
                    throw new BuildException();
                default:
                    break upper;
            }
        }
        node_end("number");
    }
    private void node_2_1() throws BuildException {
        if(getCharState(toTrans.charAt(index))==2) {
            sb.append(toTrans.charAt(index));
            index++;
            node_2_2();
        }
        else
            throw new BuildException();
    }
    private void node_2_2() throws BuildException {
        upper:for(; index <toTrans.length(); index++) {
            switch (getCharState(toTrans.charAt(index))){
                case 2:
                    sb.append(toTrans.charAt(index));
                    break;
                case -1:
                    throw new BuildException();
                default:
                    break upper;
            }
        }
        node_end("number");
    }
    private void node_3() throws BuildException {
        upper:for(; index <toTrans.length(); index++) {
            switch (getCharState(toTrans.charAt(index))){
                case 3: case 4:
                    if(markWords.containsKey(sb.toString()+toTrans.charAt(index))) {
                        sb.append(toTrans.charAt(index));
                        break;
                    }
                    else
                        break upper;
                case -1:
                    throw new BuildException();
                default:
                    break upper;
            }
        }
        node_end(sb.toString());
    }
    private void node_end(String type) {
        Word w=new Word(type,sb.toString(),line,list/2);
        symbolList.add(w);
        sb=new StringBuilder();
    }

    private int getCharState(char c){
        list++;
        switch (c){
            case '\n':
                list=0;line++;
            case ' ':case ';':case '\r':
                return 0;
            case 'a':case 'b':case 'c':case 'd':case 'e':
            case 'f':case 'g':case 'h':case 'i':case 'j':
            case 'k':case 'l':case 'm':case 'n':case 'o':
            case 'p':case 'q':case 'r':case 's':case 't':
            case 'u':case 'v':case 'w':case 'x':case 'y':
            case 'z':case 'A':case 'B':case 'C':case 'D':
            case 'E':case 'F':case 'G':case 'H':case 'I':
            case 'J':case 'K':case 'L':case 'M':case 'N':
            case 'O':case 'P':case 'Q':case 'R':case 'S':
            case 'T':case 'U':case 'V':case 'W':case 'X':
            case 'Y':case 'Z': case '_':
                return 1;
            case '0':case '1':case '2':case '3':case '4':
            case '5':case '6':case '7':case '8':case '9':
                return 2;
            case '=':case '<':case '>':case '%':
            case '/':case '*':case 'ε':case '|':
            case '(':case ')':case '+':
            case ',':
                return 3;
            case '.':
                return 4;
        }

        return -1;
    }

    public void setKeyWords(Set<String> keyWords) {
        this.keyWords=keyWords;
    }

    public void setMarkWords() {
        markWords.put(">>","right");
        markWords.put("<<","left");
        markWords.put("++","dplus");
        markWords.put("--","dminus");
        markWords.put("==","equal");
        markWords.put("!=","unequal");
        markWords.put("<=","lessequal");
        markWords.put(">=","moreequal");
        markWords.put("+=","plusequal");
        markWords.put("-=","minusequal");
        markWords.put("*=","multiequal");
        markWords.put("/=","divequal");
        markWords.put("+","plus");
        markWords.put("-","minus");
        markWords.put("=","assign");
        markWords.put("*","multi");
        markWords.put("/","div");
        markWords.put("<","less");
        markWords.put(">","more");
        markWords.put("(","leftbucket");
        markWords.put(")","rightbucket");
        markWords.put("\"","duoquo");
        markWords.put("\'","sigquo");
        markWords.put(",","comma");
        markWords.put(".","period");
    }
}
