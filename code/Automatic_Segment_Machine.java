import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;

/**
 * @author zhongziming
 * 两个引号当中的所有字符应当视为一个String，
 * 然后通过栈来处理String。即，读到第一个引号时，以后的字符全部放到一处，等到第二个匹配的引号出现时
 */
public class Automatic_Segment_Machine {
    private HashSet<String> WordMap;
    private LinkedList<Word> LW;//主SQL单词序列
    private Coolean status;
    private int nowline,nowlist;
    public Automatic_Segment_Machine(HashSet<String> map)
    {
        WordMap=map;
        nowline=1;
        nowlist=0;
    }

    public LinkedList<Word> Segment(String content) throws Exception{
        LinkedList<Word> sonLW=new LinkedList<>();
        LW=sonLW;
        StringBuffer toWord=new StringBuffer();
        status=Coolean.stop;

        for (int i = 0; i < content.length(); i++) {
            nowlist++;
            char c=content.charAt(i);
            Coolean nowstatus=this.c_BuildWord(c);
            boolean charStop=charStop(nowstatus);
            boolean isStop=charStop;

            if(isStop){
                if(toWord.length()>0) {
                    String isWord = toWord.toString();
                    if (WordMap.contains(isWord)) {
                        create_word_and_add(isWord, isWord);
                    } else {
                        build_undefined_var(isWord);
                    }
                    if (c == '\'') {
                        i += Stringinquotation(i, content);
                    }
                    toWord.delete(0, toWord.length());
                }
            }
            else if(status.equals(Coolean.mark)){//这样写事实上隐含了status与nowstatus都是mark的含义，又简化了判断
                //对于mark，要考虑到可能出现的多个字符连用成一个符号 与 多个字符连用后是多个符号的问题。
                //如果没有这样的问题则可以考虑进入下一个分支，增加mark单词的长度。
                if(!WordMap.contains(toWord.toString()+c)){
                    create_word_and_add(toWord.toString(),toWord.toString());
                    toWord.delete(0, toWord.length());
                }
            }
            if(!nowstatus.equals(Coolean.stop)){
                toWord.append(c);
            }
            status=nowstatus;
        }

        create_word_and_add(";",null);
        LW=null;
        return sonLW;
    }

    private void build_undefined_var(String isWord) throws Language_error
    {
        boolean nocreate=true;
        //接下来有两种可能，1.这是一个对象名称、2.这是不存在的单词
        if(status.equals(Coolean.mark))
            throw new Language_error(nowline,nowlist,isWord+"符号不存在");

        else
            create_word_and_add(isWord,null);
    }

    /*
    * 点作为一个letter被识别
    */
    private Coolean c_BuildWord(char c) throws Language_error
    {
        switch(c)
        {
            case '\n'://遇到换行符将行列数据刷新
                nowline=nowline+1;
                nowlist=0;
            case ' ':case '\r':case ';':case '\'':
            //识别为stop的时候不将当前字符列为单词
            //case '\''://但是单引号是特例，另有专门的方法处理
            return Coolean.stop;

            case '(': case ')':
            case '!':case '%':case '*':case '+':case ',':
            case '-':case '/'://46是'.'，要作为小数点保留//case '\"':
            case ':':case '<':case '=':case '>':case '^':
            return Coolean.mark;//识别为mark的时候将当前字符列为单词

            case '0':case '1':case '2':case '3':case '4':
            case '5':case '6':case '7':case '8':case '9':
            case 'a':case 'b':case 'c':case 'd':case 'e':
            case 'f':case 'g':case 'h':case 'i':case 'j':
            case 'k':case 'l':case 'm':case 'n':case 'o':
            case 'p':case 'q':case 'r':case 's':case 't':
            case 'u':case 'v':case 'w':case 'x':case 'y':
            case 'z':case 'A':case 'B':case 'C':case 'D':
            case 'E':case 'F':case 'G':case 'H':case 'I':
            case 'J':case 'K':case 'L':case 'M':case 'N':
            case 'O':case 'P':case 'Q':case 'R':case 'S':
            case 'T':case 'U':case 'V':case 'W':case 'X':
            case 'Y':case 'Z':case '.':
            return Coolean.letter;
            default:
                throw new Language_error(nowline,nowlist,"该字符是非法字符");
        }
    }

    private Integer Stringinquotation(int loopo,String toSQL) throws Language_error, null_escape_char_error {
        //loopo是引号所在的位置，包括单双引号,注意SQL只支持单引号字符串
        //反斜杠以及转义的识别是严重问题
        //String example1="'\""; 单引号和双引号区间内不同种引号的识别情况
        //char example2='"';
        char stop='\'';int loop=1;
        StringBuffer str=new StringBuffer();
        while(true)
        {
            nowlist++;
            if(loopo+loop==toSQL.length()-1)
                throw new Language_error(nowline,nowlist,"没有终结符号的字符串");

            if(toSQL.charAt(loopo+loop)=='\n')
            {
                nowline=nowline+1;
                nowlist=0;
            }
            else if(toSQL.charAt(loopo+loop)=='\\')//出现转义的情况
            {
                str.append(Escape(loopo,loop,toSQL));
                loop++;//跳过被转义的字符
            }
            else if(toSQL.charAt(loopo+loop)==stop)//应对标准形式的Oeacle单引号转义
            {
                if(toSQL.charAt(loopo+loop+1)==stop) {
                    str.append(stop);
                    loop++;
                }
                else
                    break;
            }
            else
                str.append(toSQL.charAt(loopo+loop));
            loop++;
        }
        //最后要将生成的String和String的结尾位置返回上层函数，同时将整个String视为一个"字"
        status=Coolean.letter;
        create_word_and_add("String",str.toString());
        return loop;
    }

    //转义字符处理函数
    private char Escape(int loopo,int loop,String toSQL) throws null_escape_char_error
    {
        switch(toSQL.charAt(loopo+loop+1))
        {
            //case 'a': return '\a'; //java不支持\a？
            case 'b': return '\b';
            case 'f': return '\f';
            case 'n': return '\n';
            case 'r': return '\r';
            case 't': return '\t';
            //case 'v': return '\v'; //java不支持\v？
            case '\\': return '\\';
            case '\'': return '\'';
            case '\"': return '\"';
            //case '\?': return '\?';
            default: throw new null_escape_char_error(nowline,nowlist);
        }
    }

    //生成简单SQL关键字的方法
    private void create_word_and_add(String name,String substance)
    {
        Word word;
        if(name.equals("String"))
            word=new Word(name,substance,nowline,nowlist-substance.length(),status.equals(Coolean.mark));
        else
            word=new Word(name,substance,nowline,nowlist-name.length(),status.equals(Coolean.mark));
        LW.add(word);
    }

    /**
     * @param nowstatus
     * @return
     * 如果返回true，意味着将会停止将当前字符放入临时缓冲区中；
     * 同时缓冲区的字符串将会及进行查表分词操作。
     */
    private boolean charStop(Coolean nowstatus){
        if(nowstatus.equals(Coolean.stop)) {
            return true;
        }
        else if(status.equals(Coolean.stop)){
            return false;
        }
        else if(!status.equals(nowstatus)){
                return true;
        }
        else {
            return false;
        }
    }
}

class Word 
{
    String type;
    String name;
    String substance;
    int stayline;
    int staynum;
    boolean isMark;
    public Word(String c_name,String c_substance)
    {
        this(c_name,c_substance,0,0,false);
    }

    /**
     * @param c_name
     * @param c_substance
     * @param line
     * @param list
     * @param ismark
     */
    public Word(String c_name,String c_substance,int line,int list,boolean ismark)
    {
        name=c_name;
        substance=c_substance;
        stayline=line;
        staynum=list;
        isMark=ismark;
    }

    public void setType(String type) { this.type=type; }
    public void setName(String name){ this.name=name; }
    public void setSubstance(String substance){ this.substance=substance; }
    public String getType() { return type; }
    public String getName() { return name; }
    public String getSubstance() { return this.substance; }
    public int[] getLocal()
    {
        int[] sr={stayline,staynum};
        return sr;
    }
    public boolean isMark()
    {
        return this.isMark;
    }

    @Override
    public String toString()
    {
        if(substance!=null)
            return substance;
        else
            return name;
    }

    @Override
    public int hashCode(){
        int code = 0;
        if(type!=null)
            code=code+type.hashCode();
        if(name!=null)
            code=code+name.hashCode();
        if(substance!=null)
            code=code+substance.hashCode();

        return code+stayline*staynum;
    }
}

class Coolean 
{
    public static Coolean letter=new Coolean("letter");
    public static Coolean mark=new Coolean("mark");
    //public static Coolean s_q=new Coolean("single_quotation");
    //public static Coolean barket=new Coolean("barket");
    public static Coolean stop=new Coolean("stop");
    String status;
    private Coolean(String str)
    {
        this.status=str;
    }
    @Override
    public String toString()
    {
        return status;
    }
    @Override
    public boolean equals(Object o)
    {
        if(o.hashCode()==this.hashCode())//&&this.toString().equals(o.toString()))
            return true;
        else 
            return false;
    }

    @Override
    public int hashCode() {
        int hash = 9;
        hash = 37 * hash + Objects.hashCode(this.status);
        return hash;
    }
}

class Language_error extends Exception{
    /**
     * @param word
     * @param errorException
     */
    public Language_error(Word word,String errorException)
    {
        super("Error in line "+word.getLocal()[0]+" list "+word.getLocal()[1]+":"+word.getSubstance()+errorException);
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

class null_escape_char_error extends Language_error
{
    public null_escape_char_error(int line,int list)
    {
        super(line,list,"不存在这样的转义字符");
    }
}