package oth.Util;

import com.zhzm.Exception.BuildException;
import com.zhzm.structure.Word;

import java.util.*;

public class FiniteAutoSegment {
    private Set<String> keyWords;
    private Map<String,String> markWords;
    private int index,line,list;
    private StringBuilder sb;
    private LinkedList<Word> symbolList;
    private String toTrans;

    public FiniteAutoSegment(){
        keyWords = new HashSet<>();
        markWords = new HashMap<>();
        setKeyWords();
        setMarkWords();
    }

    private void init(String str){
        symbolList = new LinkedList<>();
        sb=new StringBuilder();
        toTrans=str;
        index =0;
    }

    public LinkedList<Word> stateMachine(String str) throws BuildException {
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
                    index++;list++;
                    node_1();
                    return;
                case 2:
                    sb.append(toTrans.charAt(index));
                    index++;list++;
                    node_2();
                    return;
                case 3:case 4:
                    sb.append(toTrans.charAt(index));
                    index++;list++;
                    node_3();
                    return;
                case 5:
                    index++;list++;
                    node_5();
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
                    list++;
                    break;
                case -1:
                    throw new BuildException();
                default:
                    break upper;
            }
        }
        if(keyWords.contains(sb.toString()))
            node_end(sb.toString());
        else
            node_end("identifier");
    }
    private void node_2() throws BuildException {
        upper:for(; index <toTrans.length(); index++) {
            switch (getCharState(toTrans.charAt(index))){
                case 2:
                    sb.append(toTrans.charAt(index));
                    list++;
                    break;
                case -1:
                    throw new BuildException();
                default:
                    break upper;
            }
        }
        node_end("dignum");
    }

    private void node_3() throws BuildException {
        upper:for(; index <toTrans.length(); index++) {
            switch (getCharState(toTrans.charAt(index))){
                case 3: case 4:
                    if(markWords.containsKey(sb.toString()+toTrans.charAt(index))) {
                        sb.append(toTrans.charAt(index));list++;
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
        node_end(markWords.get(sb.toString()));
    }

    private void node_5() throws BuildException {
        upper:for(; index <toTrans.length(); index++) {
            switch (getCharState(toTrans.charAt(index))){
                case 5:
                    if(markWords.containsKey(sb.toString()+toTrans.charAt(index))) {
                        sb.append(toTrans.charAt(index));list++;
                        break upper;
                    }
                default:
                    sb.append(toTrans.charAt(index));
                    list++;
                    break;
            }
        }
        node_end(markWords.get("stringpart"));
    }

    private void node_end(String type) {
        Word w=new Word(type,sb.toString(),line,list-sb.toString().length());
        symbolList.add(w);
        sb=new StringBuilder();
    }

    private int getCharState(char c){
        switch (c){
            case '\n':
                list=0;line++;
            case ' ':case '\r':
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
            case '=':case '<':case '>':case '!':case '%':
            case '[':case ']':case '{':case '}':case '\'':
            case '?':case ':':case ',':case '/':
            case '\\':case '~':case '*':case '&':case '|':
            case '(':case ')':case '+':case '-':case ';':
                return 3;
            case '.':
                return 4;
            case '\"':
                return 5;
        }

        return -1;
    }

    private void setKeyWords() {
        keyWords.add("sqrt");
        keyWords.add("abs");
        keyWords.add("max");
        keyWords.add("min");
        keyWords.add("pow");
        keyWords.add("sin");
        keyWords.add("cos");
        keyWords.add("tan");
        keyWords.add("log");
        keyWords.add("ln");
        keyWords.add("exp");
        keyWords.add("sinh");
        keyWords.add("cosh");
        keyWords.add("tanh");
        keyWords.add("asin");//arcsin
        keyWords.add("acos");//arccos
        keyWords.add("atan");//arctan
        keyWords.add("int");
        keyWords.add("String");
        keyWords.add("double");
        keyWords.add("public");
        keyWords.add("private");
        keyWords.add("class");
        keyWords.add("function");
        keyWords.add("return");
        keyWords.add("random");

        keyWords.add("if");
        keyWords.add("else");
    }

    private void setMarkWords() {
        markWords.put(">>","right");
        markWords.put("<<","left");
        markWords.put("++","dplus");
        markWords.put("--","dminus");
        markWords.put("==","equal");
        markWords.put("!=","notequal");
        markWords.put("<=","lessequal");
        markWords.put(">=","largeequal");
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
        markWords.put(">","large");
        markWords.put("(","leftbucket");
        markWords.put(")","rightbucket");
        markWords.put("\"","duoquo");
        markWords.put("\'","sigquo");
        markWords.put(",","comma");
        markWords.put(".","period");
        markWords.put("{","leftcurbucket");
        markWords.put("}","rightcurbucket");
        markWords.put(";","semicolon");
        markWords.put("%","percent");
    }
}
