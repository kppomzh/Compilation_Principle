package lesson2;

class Word
{
    String type;
    String name;
    String substance;
    int stayline;
    int staynum;
    String spt="stipulation",nspt="move";

    public Word(String c_substance,boolean toSti)
    {
        name=toSti?spt:nspt;
        substance=c_substance;
        stayline=-1;
        staynum=-1;
    }

    /**
     * @param c_name
     * @param c_substance
     * @param line
     * @param list
     */
    public Word(String c_name,String c_substance,int line,int list)
    {
        name=c_name;
        substance=c_substance;
        stayline=line;
        staynum=list;
    }

    public void setType(String type) { this.type=type; }
    public void setName(String name){ this.name=name; }
    public void setSubstance(String substance){ this.substance=substance; }
    public String getType() { return type; }
    public String getName() { return name; }
    public String getSubstance() { return this.substance; }
    public int getLine(){
        return stayline;
    }
    public int getList(){
        return staynum;
    }

    @Override
    public String toString()
    {
        if(substance==spt)
            return name;
        else
            return substance;
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
