package com.zhzm.MakeJavaClass;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.zhzm.Exception.SemanticException;
import com.zhzm.makegrammar.GrammarFileWriter;
import com.zhzm.structure.Cell;
import com.zhzm.structure.VaribleBean;
import com.zhzm.structure.unTerminalMarkInfo;

import java.io.IOException;
import java.util.*;

public class MakeNonterminalClass {
    private String path;
    private Set<String> nonTerminal;
    private IfStmt checkEmpty;

    public MakeNonterminalClass(String classpath, Set<String> nonTerminal) {
        path = classpath;
        this.nonTerminal = nonTerminal;

//        makeAbsClass();
    }

    public void makeAbsClass() {
        CompilationUnit compilationUnit = new CompilationUnit();
        compilationUnit.setPackageDeclaration(path);
        compilationUnit.addImport(java.lang.reflect.Method.class);
        ClassOrInterfaceDeclaration nodeClass = compilationUnit.addClass("ParserTreeNode");
        nodeClass.addModifier(Modifier.Keyword.ABSTRACT);
        VariableDeclarationExpr pkg = new VariableDeclarationExpr();
        pkg.addVariable(new VariableDeclarator().setInitializer("packageStr=\"" + path + '"'));
        nodeClass.addFieldWithInitializer(String.class, "packageStr", pkg, Modifier.Keyword.PRIVATE);

        MethodDeclaration getSetMethod = nodeClass.addMethod("getSetMethod", Modifier.Keyword.PUBLIC);
        getSetMethod.setType("Method");
        getSetMethod.addParameter(String.class, "fieldName");
        BlockStmt block = getSetMethod.getBody().get();
        block.addStatement(new ReturnStmt("null"));

        GrammarFileWriter writer = new GrammarFileWriter();
        try {
            writer.writeClass(compilationUnit);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CompilationUnit makeCellClass(String className, unTerminalMarkInfo info) {
        checkEmpty = null;

        CompilationUnit compilationUnit = new CompilationUnit();
        compilationUnit.setPackageDeclaration(path);

        //生成类实体
        ClassOrInterfaceDeclaration nodeClass = compilationUnit.addClass(className);
        nodeClass.addExtendedType("ParserTreeNode");
        //生成构造方法
        ConstructorDeclaration construct = nodeClass.addConstructor(Keyword.PUBLIC);

        //创建递归访问方法
        MethodDeclaration substance = nodeClass.addMethod("getSubStance", Modifier.Keyword.PUBLIC);
        substance.addAnnotation(Override.class);
        substance.setType(Object.class);
        substance.addParameter(List.class, "attributeArray");
        substance.addThrownException(com.zhzm.Exception.SemanticException.class);
        compilationUnit.addImport(com.zhzm.Exception.SemanticException.class);


        List<Cell> cells = info.getCells();
        //遍历所有产生式cell，找到每个cell中的终结符和非终结符
        for (Cell cell : cells) {
//            找到每个cell中的终结符和非终结符
            Set<String> hasTerminal = new HashSet<>(), hasNonterminal = new HashSet<>();
            for (Cell child : cell.getCell()) {
                if (nonTerminal.contains(child.getCellName())) {
                    hasNonterminal.add(child.getCellName());
                } else if (child != Cell.epsilon)
                    hasTerminal.add(child.getCellName());
//                    buildNormalTerminal(nodeClass, child.getCellName());
            }

            String[] attributes = cell.getAttributes();
//            优先处理属性文法，之后分别处理生成普通非终结符与终结符的方法
//            任何一个经过已经在属性处理中添加了get/set方法的终结符或非终结符，都需要从临时的符号列表中移除
            if (attributes.length > 0){
                for (int i = 0; i < attributes.length; i++) {
                    int leftbucket = attributes[i].indexOf('('), rightbucket = attributes[i].indexOf(')');
                    String attrFunc = attributes[i].substring(0, leftbucket), attrContent = attributes[i].substring(leftbucket + 1, rightbucket);
                    switch (attrFunc) {
                        case "makeScope":
                            makeScope(construct, substance);
                            break;
//                    case "addScope":

                        case "isTerminal":
                            isTerminal(nodeClass, substance, cell.getCellName(), attrContent);
                            hasTerminal.remove(cell.getCellName());
                            break;


                        case "makeVarBean":
                            makeVarBean(compilationUnit, substance);
                            break;

                        case "setAttr":
                            break;
                        case "buildPara":
                            compilationUnit.getImports().add(new ImportDeclaration(path + '.' + attrContent, false, false));
                            buildPara(nodeClass,attrContent);
                            hasNonterminal.remove(attrContent);
                            break;
                        case "return":
                            break;
                    }
                }
            }

            //为终结符添加get/set方法
            for (String field : hasTerminal) {
                buildNormalTerminal(nodeClass,field);
            }

            //为非终结符添加get/set方法
            for (String field : hasNonterminal) {
                compilationUnit.getImports().add(new ImportDeclaration(path + '.' + field, false, false));

                MethodDeclaration getter, setter;
                getter = nodeClass.addMethod("get" + field, Modifier.Keyword.PUBLIC);
                getter.setType(field);
                getter.getBody().get().addStatement(new ReturnStmt(field.toUpperCase()));

                setter = nodeClass.addMethod("set" + field, Modifier.Keyword.PUBLIC);
                setter.addParameter(field, field.toUpperCase());
                BlockStmt block = setter.getBody().get();
                block.addStatement("this." + field.toUpperCase() + "=" + field.toUpperCase() + ";");
                setter.setBody(block);

                nodeClass.addField(field, field.toUpperCase(), Modifier.Keyword.PRIVATE);
            }
        }

//        for (String field : hasTerminal) {
//            if(info.getIsTerminal(field)!=null) {
//                buildasTerminal(nodeClass, field, info.getIsTerminal(field));
//                if(substance.getBody().get().getStatements().isEmpty())
//                    substance.getBody().get().addStatement(new ReturnStmt(info.getIsTerminal(field).toUpperCase()));
//            }
//            else if(info.getTerminal2function(field)!=null){
//                addReturnSub(substance);
//
//                String getClass="com.zhzm.functions."+info.getTerminal2function(field).substring(0,info.getTerminal2function(field).indexOf('.'));
//                compilationUnit.addImport(getClass);
//
//                BinaryExpr check=new BinaryExpr(new NameExpr(field.toUpperCase()),new NameExpr("null"), BinaryExpr.Operator.NOT_EQUALS);
//                Expression funcExp=new NameExpr(info.getTerminal2function(field)+"(null,null)");
//                ReturnStmt returnStmt=new ReturnStmt(funcExp);
//                IfStmt ifStmt=new IfStmt(check,new BlockStmt(new NodeList<>(returnStmt)), null);
//
//                checkEmpty.getElseStmt().get().asBlockStmt().addStatement(ifStmt);
//
////                if(!addThrow) {
////                    ThrowStmt lastElse = new ThrowStmt();
////                    lastElse.setExpression("new SemanticException()");
////                    substance.getBody().get().addStatement(lastElse);
////                    addThrow=true;
////                }
//
//                buildNormalTerminal(nodeClass,field);
//            }
//        }

        if (substance.getBody().get().getStatements().isEmpty())
            substance.getBody().get().addStatement(new ReturnStmt("null"));

        return compilationUnit;
    }


    private void buildNormalTerminal(ClassOrInterfaceDeclaration nodeClass, String field) {
        MethodDeclaration getter, setter;
        getter = nodeClass.addMethod("get" + field, Modifier.Keyword.PUBLIC);
        getter.setType("String");
        getter.getBody().get().addStatement(new ReturnStmt(field.toUpperCase()));

        setter = nodeClass.addMethod("set" + field, Modifier.Keyword.PUBLIC);
        setter.addParameter(String.class, field.toUpperCase());
        BlockStmt block = setter.getBody().get();
        block.addStatement("this." + field.toUpperCase() + "=" + field.toUpperCase() + ";");
        setter.setBody(block);

        nodeClass.addField(String.class, field.toUpperCase(), Modifier.Keyword.PRIVATE);
    }

    private void buildasTerminal(ClassOrInterfaceDeclaration nodeClass, String setName, String newField) {
        MethodDeclaration setter;

        setter = nodeClass.addMethod("set" + setName, Modifier.Keyword.PUBLIC);
        setter.addParameter(String.class, setName.toUpperCase());
        BlockStmt block = setter.getBody().get();
        block.addStatement("this." + newField.toUpperCase() + "=" + setName.toUpperCase() + ";");
        setter.setBody(block);

        if (nodeClass.getFieldByName(newField.toUpperCase()).isEmpty())
            nodeClass.addField(String.class, newField.toUpperCase(), Modifier.Keyword.PRIVATE);
    }

    private void addReturnSub(MethodDeclaration substance) {
        if (checkEmpty == null) {
            substance.getBody().get().addStatement("Object res=attributeArray.get(0);");
            NameExpr checkName = new NameExpr("canbeEmpty");
            ReturnStmt checkReturn = new ReturnStmt("res");
            BlockStmt checkElse = new BlockStmt();
            checkEmpty = new IfStmt(checkName, new BlockStmt(new NodeList<>(checkReturn)), checkElse);
            substance.getBody().get().addStatement(checkEmpty);
        }
    }

    private void Serialisierung() {

    }

    /**
     * 定义一个新的临时作用域
     *
     * @param construct
     * @param substance
     */
    private void makeScope(ConstructorDeclaration construct, MethodDeclaration substance) {
        construct.getBody().addStatement("scope.makeScope();");
        substance.getBody().get().addStatement("scope.unMakeScope();");
    }

    private void addScope(ConstructorDeclaration construct, MethodDeclaration substance) {
        construct.getBody().addStatement("scope.makeScope();");
        substance.getBody().get().addStatement("scope.unMakeScope();");
    }

    private void makeVarBean(CompilationUnit compilationUnit, MethodDeclaration substance) {
        String getClass = "com.zhzm.structure.VaribleBean";
        compilationUnit.addImport(getClass);

        substance.addThrownException(ClassNotFoundException.class);
        substance.getBody().get().addStatement("VaribleBean bean=new VaribleBean(Class.forName(\"java.lang.\"+VARTYPE.getSubStance()),VARIBLE);");
        substance.getBody().get().addStatement("if(ASSIGNBACK!=null){bean.setConstant(ASSIGNBACK.getSubStance());}");
//        substance.getBody().get().addStatement("    ");
        substance.getBody().get().addStatement("return bean;");

    }

    private void isTerminal(ClassOrInterfaceDeclaration nodeClass, MethodDeclaration substance, String terminalName, String setfuncName) {
        buildasTerminal(nodeClass, setfuncName, terminalName);
        if (substance.getBody().get().getStatements().isEmpty())
            substance.getBody().get().addStatement(new ReturnStmt(terminalName.toUpperCase()));
    }

    private void buildPara(ClassOrInterfaceDeclaration nodeClass,String content){
        MethodDeclaration getter, setter;
        getter = nodeClass.addMethod("get" + content, Modifier.Keyword.PUBLIC);
        getter.setType("List<"+content+'>');
        getter.getBody().get().addStatement(new ReturnStmt(content.toUpperCase()));

        setter = nodeClass.addMethod("set" + content, Modifier.Keyword.PUBLIC);
        setter.addParameter(content, content.toUpperCase());
        BlockStmt block = setter.getBody().get();
        block.addStatement("this." + content.toUpperCase() + "=" + content.toUpperCase() + ";");
        setter.setBody(block);

        nodeClass.addField("List<"+content+'>', content.toUpperCase(), Modifier.Keyword.PRIVATE);
    }
}
