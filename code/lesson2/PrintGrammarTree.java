package lesson2;

public class PrintGrammarTree {
    private static String indentation="    ",line="——";

    public static <T> void print(Node node){
        print(node,0);
    }
    private static <T> void print(Node node, int level){
        String preStr = "";     // 打印前缀
        for (int i = 0; i < level; i++) {
            preStr += "    ";
        }

        for (int i = 0; i < node.getChild().size(); i++) {
            Node t = node.getChild().get(i);
            System.out.println(preStr + "-" + t.getContent().toString());

            if (!t.getChild().isEmpty()) {
                print(t, level + 1);
            }
        }
    }
}
