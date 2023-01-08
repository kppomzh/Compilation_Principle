package com.zhzm.makegrammar;


import com.zhzm.Exception.BuildException;
import com.zhzm.MakeJavaClass.MakeNonterminalClass;
import com.zhzm.structure.Cell;
import com.zhzm.structure.GrammarTable;
import com.zhzm.structure.Word;
import com.zhzm.structure.unTerminalMarkInfo;

import java.io.IOException;
import java.util.*;

/**
 * 不动点计算
 */
public class GrammarMaker {
    private Map<String, Integer> firstCollCount, followCollCount;
    private Map<String, unTerminalMarkInfo> grammarInfo;
    private ProductionSegment segment;
    private Set<String> terminal, nonterminal, passNonterminal;
    private GrammarFileWriter writer;

    private MakeNonterminalClass classMaker;

    public GrammarMaker() {
        firstCollCount = new HashMap<>();
        followCollCount = new HashMap<>();
        grammarInfo = new HashMap<>();
        terminal = new HashSet<>();
        nonterminal = new HashSet<>();
        passNonterminal = new HashSet<>();
        writer = new GrammarFileWriter();
    }

    /**
     * 用于语法定义文件的解析
     *
     * @param lines
     * @return
     */
    public Map<String, unTerminalMarkInfo> makeConbinationGrammer(Collection<String> lines) {
        Map<String, unTerminalMarkInfo> productions = new HashMap<>();
        for (String rule : lines) {
            //切割表达式左右两边
            String[] ruleStrs = splitRule(rule), attrs;
            String prodStr = ruleStrs[0].strip(), prod = ruleStrs[1].strip();
            if (ruleStrs.length > 2) {
                //切割文法属性
                attrs = ruleStrs[2].strip().split(";");
            } else {
                attrs = new String[0];
            }

            if (!productions.containsKey(prodStr)) {
                unTerminalMarkInfo info = new unTerminalMarkInfo();
                productions.put(prodStr, info);
            }
            unTerminalMarkInfo info = productions.get(prodStr);
            info.addGrammarList(prod, attrs);
        }
        return productions;
    }

    public void setGrammar(Map<String, unTerminalMarkInfo> grammarList) {
        this.grammarInfo = grammarList;
        segment = new ProductionSegment();
        segment.setKeyWords(grammarList.keySet());
        for (String key : grammarList.keySet()) {
            firstCollCount.put(key, 0);
            followCollCount.put(key, 0);
        }
        nonterminal = grammarList.keySet();
        classMaker = new MakeNonterminalClass("com.zhzm.grammarclass", nonterminal);
    }

    public GrammarTable startMake() throws BuildException, IOException {
        for (String unterminal : nonterminal) {
            unTerminalMarkInfo info = grammarInfo.get(unterminal);
            for (String rule : info.getRules()) {
                makeCells(unterminal, rule, info.getAttributes(rule));
            }
            //生成类，并将将Java代码写入文件
            writer.writeClass(classMaker.makeCellClass(unterminal, info));
        }
        countFirstColl();
        countFollowColl();
        return makeTable();
    }

    /**
     * @param unterminal
     * @param production
     * @throws BuildException 根据产生式右部字符串整理数据结构
     */
    private void makeCells(String unterminal, String production, String[] attributes) throws BuildException {
        List<Word> words = segment.stateMachine(production);
        Cell tCell = new Cell(unterminal);
        tCell.setAttribute(attributes);

        if (production.equals("ε")) {
            tCell.addCell(Cell.epsilon);
            passNonterminal.add(unterminal);
        } else {
            for (Word word : words) {
                tCell.addCell(word);
                if (!nonterminal.contains(word.getSubstance()))
                    terminal.add(word.getSubstance());
            }
        }

        grammarInfo.get(unterminal).addCells(tCell);
    }

    /**
     * 遍历所有产生式，扫描非终结符，构造first集
     * error：存在未被加入first集合的
     */
    private void countFirstColl() {
        boolean reCount = true;
        while (reCount) {
            reCount = false;
            for (String unterminal : nonterminal) {
                List<Cell> cells = grammarInfo.get(unterminal).getCells();
                unTerminalMarkInfo info = grammarInfo.get(unterminal);

                for (Cell cell : cells) {
                    String firstMark = cell.getFirstMark();
                    if (nonterminal.contains(firstMark)) {
                        info.addFirstSet(grammarInfo.get(firstMark).getFirstSet());
                    } else {
                        info.addFirstSet(firstMark);
                    }
                }

                if (firstCollCount.get(unterminal) != grammarInfo.get(unterminal).getFirstSize()) {
                    reCount = true;
                    firstCollCount.put(unterminal, grammarInfo.get(unterminal).getFirstSize());
                }
            }
        }
    }

    /**
     * 遍历所有产生式并扫描，之后比较每个非终结符的follow集数量是否变化
     */
    private void countFollowColl() {
        boolean reCount = true;
        while (reCount) {
            reCount = false;
            for (String unterminal : nonterminal) {
                //这里获取的是非终结符对应的第一层产生式
                List<Cell> cells = grammarInfo.get(unterminal).getCells();

                for (Cell cell : cells) {
                    readCellFollow(cell, new HashSet<>());
                }
            }
            for (String unterminal : nonterminal) {
                if (followCollCount.get(unterminal) != grammarInfo.get(unterminal).getFollowSize()) {
                    reCount = true;
                    followCollCount.put(unterminal, grammarInfo.get(unterminal).getFollowSize());
                }
            }
        }
    }

    /**
     * @param uCell 遍历产生式及其子句，并扫描其中的非终结符后的终结符
     */
    private void readCellFollow(Cell uCell, Set<String> follows) {
        grammarInfo.get(uCell.getCellName()).addFollowSet(follows);

        List<Cell> cells = uCell.getCell();
        for (int i = 0; i < cells.size() - 1; i++) {
            Cell cell = cells.get(i);
            if (nonterminal.contains(cell.getCellName())) {
                unTerminalMarkInfo info = grammarInfo.get(cell.getCellName());
                //非终结符后第一个符号是非终结符
                if (nonterminal.contains(cells.get(i + 1).getCellName())) {
                    grammarInfo.get(cell.getCellName()).addFollowSet(makeFollowSet(grammarInfo.get(cells.get(i + 1).getCellName())));
                }
                //非终结符后是终结符号
                else {
                    info.addTerminaltoFollowSet(cells.get(i + 1).getCellName());
                }
            }
        }

        Cell cell = cells.get(cells.size() - 1);
        if (nonterminal.contains(cell.getCellName())) {
            //将该非终结符的follow集加入到最后一个非终结符中
            grammarInfo.get(cell.getCellName()).addFollowSet(grammarInfo.get(uCell.getCellName()).getFollowSet());
        }
    }

    /**
     * @param info
     * @return 对于出现循环的非终结符，认为它的first中默认带有 ε
     */
    private Set<String> makeFollowSet(unTerminalMarkInfo info) {
        if (info.getFirstSet().contains("ε")) {
            Set<String> res = new HashSet<>();
            res.addAll(info.getFirstSet());
            res.addAll(info.getFollowSet());
            res.remove("ε");
            return res;
        } else
            return info.getFirstSet();
    }

    /**
     * @return 根据first集与follow集填表
     */
    private GrammarTable makeTable() {
        GrammarTable res = new GrammarTable(nonterminal, terminal);

        for (String unterminal : nonterminal) {
            unTerminalMarkInfo info = grammarInfo.get(unterminal);
            List<Cell> prods = info.getCells();

            for (Cell prod : prods) {
                List<Cell> cells = prod.getCell();
                if (cells.get(0) == Cell.epsilon) {
                    Set<String> follow = info.getFollowSet();
                    for (String fTerminal : follow) {
                        res.setProduct(unterminal, fTerminal, new LinkedList<>());  //只要在这里加入一个空的列表就可以自然的处理空表达式
                    }
                } else {
                    List<String> marks = new ArrayList<>();
                    for (Cell cell : cells) {
                        marks.add(cell.getCellName());
                    }

                    if (terminal.contains(marks.get(0))) {
                        res.setProduct(unterminal, marks.get(0), marks);
                    } else {
                        Set<String> selectSet=makeSelectSet(marks.get(0));
                        for (String fTerminal : selectSet) {
                            res.setProduct(unterminal, fTerminal, marks);
                        }
                    }
                }
            }
        }

        for (String pass : passNonterminal) {
            res.setProduct(pass, "#", new LinkedList<>());
        }

        return res;
    }

    private Set<String> makeSelectSet(String unterminal) {
        unTerminalMarkInfo info = grammarInfo.get(unterminal);
        Set<String> res=new HashSet<>();

        res.addAll(info.getFirstSet());
        if(info.getFirstSet().contains(Cell.epsilon.getCellName())){
            res.addAll(info.getFollowSet());
            res.remove(Cell.epsilon.getCellName());
        }

        return res;
    }

    private String[] splitRule(String rule) {
        String[] ruleStrs;
        int firstpoint = rule.indexOf(':');
        int secondpoint = rule.indexOf(':', firstpoint + 1);
        if (secondpoint != -1) {
            ruleStrs = new String[3];
            ruleStrs[1] = rule.substring(firstpoint + 1, secondpoint);
            ruleStrs[2] = rule.substring(secondpoint + 1);
        } else {
            ruleStrs = new String[2];
            ruleStrs[1] = rule.substring(firstpoint + 1);
        }
        ruleStrs[0] = rule.substring(0, firstpoint);
        return ruleStrs;
    }
}
