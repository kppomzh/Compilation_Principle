package com.zhzm.structure;

import com.zhzm.Exception.UndefineVaribleException;
import com.zhzm.Exception.VarDefineRepeateException;

import java.util.*;

public class VaribleScope {

    private Hashtable<String, VaribleBean> scope;

    private Stack<Set<String>> tempScopeStack;

    private Set<String> tempScope;

    public VaribleScope() {
        scope = new Hashtable<>();
        tempScopeStack=new Stack<>();
    }

    public void makeScope() {
        tempScopeStack.push(tempScope);
        tempScope = new HashSet<>();
    }

    public void unMakeScope() {
        for(String varName:tempScope){
            scope.remove(varName);
        }
        tempScope=tempScopeStack.pop();
    }

    public void addVarible(String varName, VaribleBean bean) throws VarDefineRepeateException {
        if (scope.contains(varName)) {
            throw new VarDefineRepeateException();
        } else if (tempScope != null) {
            tempScope.add(varName);
        }
        scope.put(varName, bean);
    }

    public VaribleBean findVarible(String varName) throws UndefineVaribleException {
        if (scope.contains(varName)) {
            return scope.get(varName);
        } else {
            throw new UndefineVaribleException();
        }
    }
}
