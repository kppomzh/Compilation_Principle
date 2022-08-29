package lesson3.structure;

import java.util.*;

/**
 * 一个非终结符所应当包含的分析信息
 */
public class unTerminalMarkInfo {
    private Set<String> firstSet;
    private Set<String> followSet;

    public unTerminalMarkInfo(){
        firstSet=new HashSet<>();
        followSet=new HashSet<>();
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
}
