package com.zhzm.structure;

public class VaribleBean {
    //记录变量的Java类型
    private Class baseClass;
    //记录变量名
    private String varName;
    //记录变量内容
    private Object constant;


    public VaribleBean(Class baseClass, String varName) {
        this(baseClass, varName, null);
    }

    public VaribleBean(Class baseClass, String varName, Object constant) {
        this.baseClass = baseClass;
        this.varName = varName;
        this.constant = constant;
    }

    public void setConstant(Object constant) {
        this.constant = constant;
    }

    public Class getBaseClass() {
        return baseClass;
    }

    public String getVarName() {
        return varName;
    }

    public Object getConstant() {
        return constant;
    }
}
