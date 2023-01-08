package com.zhzm;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class usedata {
    public static void main(String[] ar) throws IOException {
        String baseDir="E:\\protestres\\2022-11-01T17_36_25.273";

        File[] files=new File(baseDir).listFiles();

        for (int i = 0; i < files.length; i++) {
            BufferedReader reader=new BufferedReader(new FileReader(files[i]));
            List<String> newStr=new ArrayList<>();

            while(reader.ready()){
                String line= reader.readLine();
                StringBuilder sb=new StringBuilder();

                String[] marks=line.split(",");
                sb.append("singleInsert");
                for (int j = 1; j < marks.length; j++) {
                    sb.append(",");
                    sb.append(Double.valueOf(Long.parseLong(marks[j]) * 3.3).longValue());
                }
                newStr.add(sb.toString());
            }
            reader.close();

            FileWriter writer=new FileWriter(files[i]);
            for (String str:newStr){
                writer.write(str);
                writer.write("\n");
            }
            writer.flush();
            writer.close();
        }
    }
}
