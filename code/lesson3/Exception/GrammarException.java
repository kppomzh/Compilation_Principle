package lesson3.Exception;

import lesson3.structure.Word;

public class GrammarException extends RuntimeException{
    public GrammarException(Word word){
        super("Grammar Error at line: "+word.getLine()+",list: "+word.getList()+",word name is: "+word.getName());
    }
}
