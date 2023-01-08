package oth.Util;

import java.io.*;

public class SerialUtil {
    public void writeSerObj(Object obj, String grammarFileName){
        try
        {
            FileOutputStream fileOut =
                    new FileOutputStream("serial/"+grammarFileName+".ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(obj);
            out.close();
            fileOut.close();
//            System.out.printf("Serialized data is saved in /tmp/employee.ser");
        }catch(IOException i)
        {
            i.printStackTrace();
        }
    }

    public Object readSerObj(String grammarFileName) throws ClassNotFoundException, IOException {
        Object o;
        try
        {
            FileInputStream fileIn = new FileInputStream("serial/"+grammarFileName+".ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            o=in.readObject();
            in.close();
            fileIn.close();
        }catch(IOException i)
        {
            i.printStackTrace();
            throw i;
        }catch(ClassNotFoundException c)
        {
            System.err.println("class not found");
            throw c;
        }
        return o;
    }
}
