package lesson2;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FiniteAutoSegment {
    private Set<String> keyWords,markWords;
    private int index,line,list;
    private StringBuilder sb;
    private LinkedList<Word> symbolList;
    private String toTrans;

    public FiniteAutoSegment(){
        keyWords = new HashSet<>();
        markWords = new HashSet<>();
        setKeyWords();
        setMarkWords();
    }

    private void init(String str){
        symbolList = new LinkedList<>();
        sb=new StringBuilder();
        toTrans=str;
        index =0;
    }

    public List<Word> stateMachine(String str) throws Exception {
        init(str);
        while (index <str.length())
            node_0();
        return symbolList;
    }

    private void node_0() throws Exception {
        for(; index <toTrans.length(); index++){
            switch (getCharState(toTrans.charAt(index))){
                case 1:
                    sb.append(toTrans.charAt(index));
                    index++;
                    node_1();
                    return;
                case 2:
                    sb.append(toTrans.charAt(index));
                    index++;
                    node_2();
                    return;
                case 3:case 4:
                    sb.append(toTrans.charAt(index));
                    index++;
                    node_3();
                    return;
                case -1:
                    throw new Exception();
                default:
                    break;
            }
        }
    }
    private void node_1() throws Exception {
        upper:for(; index <toTrans.length(); index++) {
            switch (getCharState(toTrans.charAt(index))){
                case 1: case 2:
                    sb.append(toTrans.charAt(index));
                    break;
                case -1:
                    throw new Exception();
                default:
                    break upper;
            }
        }
        if(keyWords.contains(sb.toString()))
            node_end("keyword");
        else
            throw new Exception();
    }
    private void node_2() throws Exception {
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
                    throw new Exception();
                default:
                    break upper;
            }
        }
        node_end("number");
    }
    private void node_2_1() throws Exception {
        if(getCharState(toTrans.charAt(index))==2) {
            sb.append(toTrans.charAt(index));
            index++;
            node_2_2();
        }
        else
            throw new Exception();
    }
    private void node_2_2() throws Exception {
        upper:for(; index <toTrans.length(); index++) {
            switch (getCharState(toTrans.charAt(index))){
                case 2:
                    sb.append(toTrans.charAt(index));
                    break;
                case -1:
                    throw new Exception();
                default:
                    break upper;
            }
        }
        node_end("number");
    }
    private void node_3() throws Exception {
        upper:for(; index <toTrans.length(); index++) {
            switch (getCharState(toTrans.charAt(index))){
                case 3: case 4:
                    if(markWords.contains(sb.toString()+toTrans.charAt(index))) {
                        sb.append(toTrans.charAt(index));
                        break;
                    }
                    else
                        break upper;
                case -1:
                    throw new Exception();
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
            case '/':case '*':case '&':case '|':
            case '(':case ')':case '+':case '-':
            case ',':
                return 3;
            case '.':
                return 4;
        }

        return -1;
    }

    public void setKeyWords() {
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
    }

    public void setMarkWords() {
        markWords.add(">>");
        markWords.add("<<");
    }
}
