package com.zhzm.makegrammar;

import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GrammarFileWriter {

    private static String basePath = "src/main/java1/";

    public void writeClass(CompilationUnit unit) throws IOException {
        String fileContent = String.valueOf(unit);
        String[] advPath = unit.getPackageDeclaration().get().getName().toString().split("\\.");
        String filename = unit.getType(0).getName().asString();

        StringBuilder fullPath = new StringBuilder();
        if (advPath.length > 0) {
            fullPath.append(basePath).append(advPath[0]);
            for (int i = 1; i < advPath.length; i++) {
                fullPath.append('/').append(advPath[i]);
            }
        }
        fullPath.append('/').append(filename).append(".java");

        File classFile = new File(fullPath.toString());
        if (!classFile.getParentFile().exists())
            classFile.getParentFile().mkdirs();

        FileWriter writer=new FileWriter(classFile);
        writer.write(fileContent);
        writer.flush();
    }
}
