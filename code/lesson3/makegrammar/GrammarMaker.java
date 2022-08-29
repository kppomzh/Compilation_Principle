package lesson3.makegrammar;

import lesson3.Exception.AnalyzeException;
import lesson3.GrammarTable;
import lesson3.structure.Cell;
import lesson3.structure.Word;
import lesson3.structure.unTerminalMarkInfo;

import java.util.*;

/**
 * 不动点计算
 */
public class GrammarMaker {
    private Map<String,Integer> firstCollCount,followCollCount;
    private Map<String,List<String>> grammarList;
    private Map<String,List<Cell>> grammarCells;
    private Map<String,unTerminalMarkInfo> grammarInfo;
    private ProductionSegment segment;
    private Set<String> terminal,nonterminal,passNonterminal;

    public GrammarMaker(){
        firstCollCount=new HashMap<>();
        followCollCount=new HashMap<>();
        grammarCells=new HashMap<>();
        grammarInfo=new HashMap<>();
        terminal=new HashSet<>();
        nonterminal=new HashSet<>();
        passNonterminal=new HashSet<>();
//        terminal.add("ε");
    }

    public void setGrammar(Map<String,List<String>> grammarList){
        this.grammarList=grammarList;
        segment=new ProductionSegment();
        segment.setKeyWords(grammarList.keySet());
        for(String key:grammarList.keySet()){
            grammarCells.put(key,new ArrayList<>());
            firstCollCount.put(key,0);
            followCollCount.put(key,0);
            grammarInfo.put(key,new unTerminalMarkInfo());
        }
        nonterminal=grammarList.keySet();
    }

    public GrammarTable startMake() throws AnalyzeException {
        for(String unterminal:grammarList.keySet()) {
            for(String rule:grammarList.get(unterminal)) {
                makeCells(unterminal,rule);
            }
        }
        countFirstColl();
        countFollowColl();
        return makeTable();
    }

    /**
     * @param unterminal
     * @param production
     * @throws AnalyzeException
     * 根据产生式右部字符串整理数据结构
     */
    private void makeCells(String unterminal,String production) throws AnalyzeException {
        List<Word> words=segment.stateMachine(production);
        Cell tCell=new Cell(unterminal);

        if(production.equals("ε")){
            tCell.addCell(Cell.epsilon);
            passNonterminal.add(unterminal);
        }
        else {
            for (Word word : words) {
                tCell.addCell(word);
                if (!nonterminal.contains(word.getSubstance()))
                    terminal.add(word.getSubstance());
            }
        }

        grammarCells.get(unterminal).add(tCell);
    }

    /**
     * 遍历所有产生式，扫描非终结符，构造first集
     */
    private void countFirstColl(){
        boolean reCount=true;
        while(reCount) {
            reCount=false;
            for (String unterminal : nonterminal) {
                List<Cell> cells = grammarCells.get(unterminal);
                unTerminalMarkInfo info = grammarInfo.get(unterminal);

                for (Cell cell : cells) {
                    String firstMark = cell.getFirstMark();
                    if (nonterminal.contains(firstMark)) {
                        info.addFirstSet(grammarInfo.get(firstMark).getFirstSet());
                    } else {
                        info.addFirstSet(firstMark);
                    }
                }

                if(firstCollCount.get(unterminal)!=grammarInfo.get(unterminal).getFirstSize()){
                    reCount=true;
                    firstCollCount.put(unterminal,grammarInfo.get(unterminal).getFirstSize());
                }
            }
        }
    }

    /**
     * 遍历所有产生式并扫描，之后比较每个非终结符的follow集数量是否变化
     */
    private void countFollowColl(){
        boolean reCount=true;
        while(reCount) {
            reCount=false;
            for (String unterminal : nonterminal) {
                //这里获取的是非终结符对应的第一层产生式
                List<Cell> cells = grammarCells.get(unterminal);

                for (Cell cell : cells) {
                    readCellFollow(cell,new HashSet<>());
                }
            }
            for (String unterminal : nonterminal) {
                if(followCollCount.get(unterminal)!=grammarInfo.get(unterminal).getFollowSize()){
                    reCount=true;
                    followCollCount.put(unterminal,grammarInfo.get(unterminal).getFollowSize());
                }
            }
        }
    }

    /**
     * @param uCell
     * 遍历产生式及其子句，并扫描其中的非终结符后的终结符
     */
    private void readCellFollow(Cell uCell,Set<String> follows){
        grammarInfo.get(uCell.getCellName()).addFollowSet(follows);

        List<Cell> cells=uCell.getCell();
        for(int i=0;i< cells.size()-1;i++){
            Cell cell=cells.get(i);
            if(nonterminal.contains(cell.getCellName())){
                unTerminalMarkInfo info=grammarInfo.get(cell.getCellName());
                //非终结符后第一个符号是非终结符
                if(nonterminal.contains(cells.get(i+1).getCellName())) {
                    grammarInfo.get(cell.getCellName()).addFollowSet(makeFollowSet(grammarInfo.get(cells.get(i+1).getCellName())));
                }
                //非终结符后是终结符号
                else{
                    info.addTerminaltoFollowSet(cells.get(i+1).getCellName());
                }
            }
        }

        Cell cell=cells.get(cells.size()-1);
        if(nonterminal.contains(cell.getCellName())){
            //将该非终结符的follow集加入到最后一个非终结符中
            grammarInfo.get(cell.getCellName()).addFollowSet(grammarInfo.get(uCell.getCellName()).getFollowSet());
        }
    }

    /**
     * @param info
     * @return
     * 对于出现循环的非终结符，认为它的first中默认带有 ε
     */
    private Set<String> makeFollowSet(unTerminalMarkInfo info){
        if(info.getFirstSet().contains("ε")){
            Set<String> res=new HashSet<>();
            res.addAll(info.getFirstSet());
            res.addAll(info.getFollowSet());
            res.remove("ε");
            return res;
        }
        else
            return info.getFirstSet();
    }

    /**
     * @return
     * 根据first集与follow集填表
     */
    private GrammarTable makeTable(){
        GrammarTable res=new GrammarTable(nonterminal,terminal);

        for(String unterminal:nonterminal){
            List<Cell> prods=grammarCells.get(unterminal);
            unTerminalMarkInfo info=grammarInfo.get(unterminal);

            for(Cell prod:prods){
                List<Cell> cells=prod.getCell();
                if(cells.get(0)==Cell.epsilon){
                    Set<String> follow=info.getFollowSet();
                    for(String fTerminal:follow){
                        res.setProduct(unterminal,fTerminal,new LinkedList<>());  //只要在这里加入一个空的列表就可以自然的处理空表达式
                    }
                }
                else{
                    List<String> marks=new ArrayList<>();
                    for(Cell cell:cells){
                        marks.add(cell.getCellName());
                    }

                    if(terminal.contains(marks.get(0))) {
                        res.setProduct(unterminal,marks.get(0),marks);
                    }
                    else{
                        unTerminalMarkInfo fInfo=grammarInfo.get(marks.get(0));
                        for(String fTerminal: fInfo.getFirstSet()){
                            res.setProduct(unterminal,fTerminal,marks);
                        }
                    }
                }
            }
        }

        for(String pass:passNonterminal){
            res.setProduct(pass,"#",new LinkedList<>());
        }

        return res;
    }
}
