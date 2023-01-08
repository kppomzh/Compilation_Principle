package com.zhzm;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

public class ReturnTest {

    public static void main(String[] ar){
        String javaStr= """
            public class thisis {
                private String MINUS;        
                public void getSubStance() {
                    if (MINUS!=null) {
                    } else {
                        throw new SemanticException();
                    }
                }
            }
            """;
        CompilationUnit cu=new JavaParser().parse(javaStr).getResult().get();

        MethodDeclaration method=cu.getClassByName("thisis").get().getMethodsByName("getSubStance").get(0);
        method.getBody().get();
        System.out.println(method);
    }
}
