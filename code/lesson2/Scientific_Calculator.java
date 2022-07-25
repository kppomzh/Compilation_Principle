package lesson2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Stack;

public class Scientific_Calculator {
    private Stack<Node> nodeStack;
    private List<Word> words;
    private int index;
    private Node treeRootNode, upperNode;

    public static void main(String[] args) {
        FiniteAutoSegment segment = new FiniteAutoSegment();
        Scientific_Calculator calc;
        String[] ag = {
//                "1+()*3",
//                "1+cos()*3",
                "1*((3*((1+2)-4)+5)-13)",
                "(1+3.14)*4",
                "(sin(1)+3.14)*4",
                "(sin(1)+sin(2*3))/4",
                "pow(2,10)",
                "1+2*3"
        };
        for (int i = 0; i < ag.length; i++) {
            try {
                List<Word> words = segment.stateMachine(ag[i]);
                calc = new Scientific_Calculator();
                calc.setWords(words);
                Node node = calc.derivation();

//                PrintGrammarTree.print(node);

                calc.stipulation(calc.treeRootNode);
                System.out.println(calc.nodeStack.pop().getContent().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("--------------------------------------");
        }
    }

    //推导过程
    public Node derivation() throws Language_error {
        nodeStack = new Stack<>();
        index = 0;
        treeRootNode = new Node(new Word("S", true));
        upperNode = treeRootNode;

        state_0();

        return treeRootNode;
    }

    // E->TE'
    private void state_0() throws Language_error {
        Node n = new Node(new Word("E", true));
        upperNode.addChildNode(n);
        nodeStack.push(upperNode);
        upperNode = n;
        switch (words.get(index).getName()) {
            case "number":
            case "(":
            case "keyword":
                state_1();
                state_3();
                break;
            default:
                throw new Language_error(words.get(index), "wrong");
        }
        upperNode = nodeStack.pop();
    }

    // T->FT'
    private void state_1() throws Language_error {
        Node n = new Node(new Word("T", true));
        upperNode.addChildNode(n);
        nodeStack.push(upperNode);
        upperNode = n;
        switch (words.get(index).getName()) {
            case "number":
            case "(":
            case "keyword":
                state_2();
                state_4();
                break;
            default:
                throw new Language_error(words.get(index), "wrong");
        }
        upperNode = nodeStack.pop();
    }

    // F->U|(E)|i
    private void state_2() throws Language_error {
        Node n = new Node(new Word("F", true));
        upperNode.addChildNode(n);
        nodeStack.push(upperNode);
        upperNode = n;
        Word w = words.get(index);

        switch (w.getName()) {
            case "number":
                Node num = new Node(w);
                index++;
                upperNode.addChildNode(num);
                break;
            case "(": //实际上括号只是用于改变优先级，并没有任何计算的作用，因此这里直接放弃掉了所有括号的分析
                index++;
                state_0();
                index++;
                break;
            case "keyword":
                state_5();
                break;
            default:
                throw new Language_error(w, "wrong");
        }
        upperNode = nodeStack.pop();
    }

    //E'->[+|-]TE'
    private void state_3() throws Language_error {
        Node n = new Node(new Word("E'", false));
        upperNode.addChildNode(n);
        nodeStack.push(upperNode);
        upperNode = n;

        if (index < words.size() && (words.get(index).getSubstance().equals("+") || words.get(index).getSubstance().equals("-"))) {
            words.get(index).setName("mark");
            Node mark = new Node(words.get(index));
            upperNode.addChildNode(mark);
            index++;

            state_1();
            state_3();
        }

        upperNode = nodeStack.pop();
    }

    //T'->[*|/]FT'
    private void state_4() throws Language_error {
        Node n = new Node(new Word("T'", false));
        upperNode.addChildNode(n);
        nodeStack.push(upperNode);
        upperNode = n;

        if (index < words.size() && (words.get(index).getSubstance().equals("*") || words.get(index).getSubstance().equals("/"))) {
            words.get(index).setName("mark");
            Node mark = new Node(words.get(index));
            upperNode.addChildNode(mark);
            index++;

            state_2();
            state_4();
        }

        upperNode = nodeStack.pop();
    }

    // U->keyword ( { E , }* E )
    private void state_5() throws Language_error {
        Node n = new Node(new Word("U", true));
        upperNode.addChildNode(n);
        nodeStack.push(upperNode);
        upperNode = n;

        {
            Node key = new Node(words.get(index));
            upperNode.addChildNode(key);
            index++;

            if (!words.get(index).getName().equals("("))
                throw new Language_error(words.get(index), "wrong");
            index++;//这里也要同样的放弃对括号和逗号的分析，理论上作为语法单位是应该进入语法树的，但是这里就省略处理

            state_0();

            for (; index < words.size(); ) {
                if (words.get(index).getName().equals(")")) {
                    index++;
                    break;
                } else if (!words.get(index).getName().equals(","))
                    throw new Language_error(words.get(index), "wrong");
                index++; //省略逗号
                state_0();
            }

        }
        upperNode = nodeStack.pop();
    }

    public void stipulation(Node node) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, Language_error {
        nodeStack = new Stack<>();
        sti_1(node);
    }

    private void sti_1(Node node) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, Language_error {
        int stiNum = 0;
        upper:
        for (int i = 0; i < node.getChild().size(); i++) {
            Node child = node.getChild().get(i);
            switch (child.getContent().getName()) {
                case "keyword":
                    stiNum++;
                    sti_3(node);
                    break upper;
                case "number":
                    stiNum++;
                    nodeStack.push(child);
                    break;
                case "move":
                    stiNum += sti_2(child);
                    break;
                case "stipulation":
                    sti_1(child);
                    stiNum++;
                    break;

            }
        }

        while (stiNum > 1) {
            String sec = nodeStack.pop().getContent().toString(),
                    mark = nodeStack.pop().getContent().toString(),
                    fst = nodeStack.pop().getContent().toString();
            String res = calcExpr(fst, mark, sec);

            Node newNode = new Node(new Word("number", res, node.getContent().getLine(), node.getContent().getList()));
            nodeStack.push(newNode);

            stiNum -= 2;
        }
    }

    //move
    private int sti_2(Node node) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, Language_error {
        if (node.getChild().size() == 0)
            return 0;
        else {
            int stiNum = 0;
            for (int i = 0; i < node.getChild().size(); i++) {
                Node child = node.getChild().get(i);
                switch (child.getContent().getName()) {
                    case "mark":
                        nodeStack.push(child);
                        stiNum++;
                        break;
                    case "move":
                        stiNum += sti_2(child);
                        break;
                    case "stipulation":
                        sti_1(child);
                        stiNum++;
                        break;
                }
            }
            return stiNum;
        }
    }

    private void sti_3(Node node) throws Language_error, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        int stiNum = 0;
        for (int i = 0; i < node.getChild().size(); i++) {
            Node child = node.getChild().get(i);
            switch (child.getContent().getName()) {
                case "keyword":
//                case "number":
                    stiNum++;
                    nodeStack.push(child);
                    break;
                case "stipulation":
                    sti_1(child);
                    stiNum++;
                    break;
            }
        }

        String[] nums = new String[stiNum - 1];
        for (int i = stiNum - 1; i > 0; i--) {
            nums[i-1] = nodeStack.pop().getContent().toString();
        }
        String res = calaFunction(
                nodeStack.pop().getContent().getSubstance(),
                nums
        );

        Node newNode = new Node(new Word("number", res, node.getContent().getLine(), node.getContent().getList()));
        nodeStack.push(newNode);
    }

    private String calcExpr(String fst, String mark, String sec) throws Language_error {
        Double fNum = Double.parseDouble(fst), sNum = Double.parseDouble(sec);
        switch (mark) {
            case "+":
                return String.valueOf(fNum + sNum);
            case "-":
                return String.valueOf(fNum - sNum);
            case "*":
                return String.valueOf(fNum * sNum);
            case "/":
                return String.valueOf(fNum / sNum);
            case "%":
                return String.valueOf(fNum % sNum);
            case ">>":
                return String.valueOf(fNum.intValue() >> sNum.intValue());
            case "<<":
                return String.valueOf(fNum.intValue() << sNum.intValue());
            default:
                throw new Language_error("caculation error");
        }
    }

    private String calaFunction(String funcName, String... nums) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method func;
        double[] numsd = trans2DoubleArray(nums);
        switch (nums.length) {
            case 0:
                func = Math.class.getMethod(funcName);
                return String.valueOf(func.invoke(null));
            case 1:
                func = Math.class.getMethod(funcName, double.class);
                return String.valueOf(func.invoke(null, numsd[0]));
            case 2:
                func = Math.class.getMethod(funcName, double.class, double.class);
                return String.valueOf(func.invoke(null, numsd[0], numsd[1]));
            default:
                throw new NoSuchMethodException();
        }
    }

    private double[] trans2DoubleArray(String[] str) {
        double[] result = new double[str.length];
        for (int i = 0; i < str.length; i++) {
            result[i] = Double.parseDouble(str[i]);
        }
        return result;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }
}
