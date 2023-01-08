package com.zhzm.languagefunctions;

import com.zhzm.Exception.RunningCastException;
import com.zhzm.Exception.TypeCastException;
import com.zhzm.Exception.VarDefineRepeateException;
import com.zhzm.structure.VaribleBean;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

public class RunningScope {
    private LinkedList<Map<String, VaribleBean>> scopeList;

    public RunningScope() {
        scopeList = new LinkedList<>();
    }

    public void makeScope() {
        scopeList.addFirst(new HashMap<>());
    }

    public void dropScope() {
        scopeList.removeFirst();
    }

    public void addVariable(Class baseClass, String varName, Object constant) throws VarDefineRepeateException {
        Map<String, VaribleBean> scopeFloor = scopeList.getFirst();

        if (scopeFloor.containsKey(varName)) {
            throw new VarDefineRepeateException();
        } else {
            scopeList.getFirst().put(varName, new VaribleBean(baseClass, varName, constant));
        }
    }

    public <T> void checkVariableType(Class<T> clazz, String varName) throws TypeCastException {
        ListIterator<Map<String, VaribleBean>> itScope = scopeList.listIterator();
        while (itScope.hasNext()) {
            Map<String, VaribleBean> scopeFloor = itScope.next();
            if (scopeFloor.containsKey(varName) && scopeFloor.get(varName).getBaseClass().getName().equalsIgnoreCase(clazz.getName())) {
                return;
            }
        }

        throw new TypeCastException();
    }

    public <T> T useVariable(Class<T> clazz, String varName) {
        ListIterator<Map<String, VaribleBean>> itScope = scopeList.listIterator();
        while (itScope.hasNext()) {
            Map<String, VaribleBean> scopeFloor = itScope.next();
            if (scopeFloor.containsKey(varName) && scopeFloor.get(varName).getBaseClass().getName().equalsIgnoreCase(clazz.getName())) {
                return clazz.cast(scopeFloor.get(varName).getConstant());
            }
        }

        throw new RunningCastException();
    }


}
