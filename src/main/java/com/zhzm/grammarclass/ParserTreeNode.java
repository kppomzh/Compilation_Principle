package com.zhzm.grammarclass;

import com.zhzm.Exception.SemanticException;
import com.zhzm.structure.VaribleScope;

import java.lang.reflect.Method;
import java.util.List;

public abstract class ParserTreeNode {
    protected boolean canbeEmpty=false;
    protected static VaribleScope scope=new VaribleScope();

    private String packageStr="com.zhzm.grammarclass.";

    public Method getSetMethod(String fieldName) throws NoSuchMethodException {
        return this.getClass().getMethod("set"+fieldName,String.class);
    }

    public Method getSetMethod(String fieldName,String className) throws NoSuchMethodException, ClassNotFoundException {
        Class c=Class.forName(packageStr+className);
        return this.getClass().getMethod("set"+fieldName,c);
    }

    public Method getGetMethod(String fieldName) throws NoSuchMethodException {
        return this.getClass().getMethod("get"+fieldName);
    }

    public abstract Object getSubStance(List<?> attributeArray) throws SemanticException, ClassNotFoundException;

    public void isEmpty() {
        this.canbeEmpty = true;
    }
}
