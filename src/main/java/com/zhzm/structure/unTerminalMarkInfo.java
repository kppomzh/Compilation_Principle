package com.zhzm.structure;

import java.util.*;

/**
 * 一个非终结符所应当包含的分析信息
 */
public class unTerminalMarkInfo {
    private Map<String, String> isTerminal;
    private Map<String,String> terminal2function;

    private Set<String> firstSet;
    private Set<String> followSet;
    private List<Cell> cells;
    private Map<String,String[]> grammarMap;

    public unTerminalMarkInfo(){
        firstSet=new HashSet<>();
        followSet=new HashSet<>();
        terminal2function=new HashMap<>();
        isTerminal=new HashMap<>();
        cells=new ArrayList<>();
        grammarMap =new HashMap<>();
    }

    public Set<String> getFollowSet() {
        return followSet;
    }

    public int getFollowSize(){
        return getFollowSet().size();
    }

    public void addFollowSet(Set<String> Set) {
        this.followSet.addAll(Set);
    }

    public void addTerminaltoFollowSet(String Terminal) {
        this.followSet.add(Terminal);
    }

    public Set<String> getFirstSet() {
        return firstSet;
    }

    public int getFirstSize(){
        return getFirstSet().size();
    }

    public void addFirstSet(Set<String> Set) {
        this.firstSet.addAll(Set);
    }
    public void addFirstSet(String firstMark) {
        this.firstSet.add(firstMark);
    }
//
//    public String getIsTerminal(String key) {
//        return isTerminal.get(key);
//    }
//
//    public String getTerminal2function(String key) {
//        return terminal2function.get(key);
//    }
//
//    public void setIsTerminal(String terminalName, String word) {
////        if(!isTerminal.containsKey(terminalName)){
////            isTerminal.put(terminalName,new ArrayList<>());
////        }
//        isTerminal.put(word,terminalName);
//    }
//
//    public void setTerminal2function(String terminal,String functionName){
//        terminal2function.put(terminal,functionName);
//    }

//    public Map<String,String[]> getGrammarMap() {
//        return grammarMap;
//    }

    public Set<String> getRules(){
        return grammarMap.keySet();
    }

    public String[] getAttributes(String rule){
        return grammarMap.get(rule);
    }

    public void addGrammarList(String grammar) {
        this.grammarMap.put(grammar,null);
    }

    //记录一条文法产生式右部以及附带的所有文法属性
    public void addGrammarList(String grammar,String[] attribute) {
        this.grammarMap.put(grammar,attribute);
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void addCells(Cell cell) {
        this.cells.add(cell);
    }
}
