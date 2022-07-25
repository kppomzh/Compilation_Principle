package lesson2;

class Language_error extends Exception{
    /**
     * @param word
     * @param errorException
     */
    public Language_error(Word word, String errorException)
    {
        super("Error in line "+word.getLine()+" list "+word.getList()+":"+word.getSubstance()+errorException);
    }

    /**
     * @param line:行
     * @param list:列
     * @param errorException
     */
    public Language_error(int line,int list,String errorException)
    {
        super("Error in line "+line+" list "+list+":"+errorException);
    }

    public Language_error(String error)
    {
        super(error);
    }
}
