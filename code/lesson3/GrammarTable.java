package lesson3;

import java.util.*;

public class GrammarTable {
    private Map<String,Integer> nonterminal,terminal;
    private List<String>[][] production;

    public GrammarTable(Set<String> nonterminal, Set<String> terminal){
        String[] nonterminalArray=nonterminal.toArray(new String[0]),
                terminalArray=terminal.toArray(new String[0]);

        this.nonterminal=new LinkedHashMap<>();
        production=new List[nonterminal.size()][];
        for (int i = 0; i < nonterminal.size(); i++) {
            this.nonterminal.put(nonterminalArray[i],i);

            production[i]=new List[terminal.size()];
        }
        this.terminal=new LinkedHashMap<>();
        for (int i = 0; i < terminal.size(); i++) {
            this.terminal.put(terminalArray[i],i);
        }
        this.terminal.put("#",terminal.size()-1);
    }

    public void setProduct(String nonterminalStr,String terminalStr,List<String> product){
        production[nonterminal.get(nonterminalStr)][terminal.get(terminalStr)]=product;
    }

    public List<String> getProduct(String nonterminalStr,String terminalStr){
        if(nonterminal.containsKey(nonterminalStr)&&terminal.containsKey(terminalStr)) {
            return production[nonterminal.get(nonterminalStr)][terminal.get(terminalStr)];
        }
        else{
            return null;
        }
    }

    public Set<String> getNonterminal(){
        return nonterminal.keySet();
    }
    public Set<String> getTerminal(){
        return terminal.keySet();
    }
}
