package lesson3;

import lesson3.Exception.AnalyzeException;
import lesson3.makegrammar.GrammarMaker;
import lesson3.makegrammar.GrammerFileReader;

import java.io.IOException;
import java.util.Collection;

public class Analyze {
    public static GrammarTable analysisProcess(String filename) throws IOException, AnalyzeException {
        GrammerFileReader reader=new GrammerFileReader("./Grammar");
        Collection<String> lines=reader.getLinesinFile(filename);

        GrammarMaker maker=new GrammarMaker();
        maker.setGrammar(GrammerFileReader.makeConbinationGrammer(lines));
        return maker.startMake();
    }
}
