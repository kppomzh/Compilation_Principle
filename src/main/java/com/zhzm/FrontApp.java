package com.zhzm;

import com.zhzm.Exception.BuildException;
import com.zhzm.makegrammar.GrammarMaker;
import com.zhzm.makegrammar.GrammerFileReader;
import com.zhzm.structure.GrammarTable;
import oth.Util.PrintGrammarObject;
import oth.Util.SerialUtil;

import java.io.IOException;
import java.util.Collection;

/**
 * Hello world!
 *
 */
public class FrontApp
{
    public static void main( String[] args ) throws BuildException, IOException {
        String grammarFileName="language.grammar";

        GrammerFileReader reader=new GrammerFileReader("./Grammar");
        Collection<String> lines=reader.getLinesinFile(grammarFileName);

        GrammarMaker maker=new GrammarMaker();
        maker.setGrammar(maker.makeConbinationGrammer(lines));

        GrammarTable table=  maker.startMake();
        PrintGrammarObject.printGrammarTable(table);

        new SerialUtil().writeSerObj(table,grammarFileName);
    }
}
