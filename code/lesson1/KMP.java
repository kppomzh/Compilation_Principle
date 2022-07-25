package lesson1;

public class KMP {
    public static void main(String[] args){
        KMP k=new KMP();

        String[] ag={
//                "ababaab",
//                "abaa",
//                "a4f5ga4f5gc",
//                "a4f5gc",
//                "a4a43aa4a434a",
//                "a4a4a4",
                "a4a43aa4a434a",
                "a4a5a4a5a4a5"
        };

        for (int i = 0; i < ag.length; i=i+2) {
            System.out.println(k.strStr(ag[i],ag[i+1]));
        }
    }

    public int strStr(String haystack, String needle) {
        if (haystack == null || needle == null ||
                haystack.length() == 0||needle.length()>haystack.length()) return -1;
        if (needle.length() == 0) return 0;

        int[] next=new int[needle.length()];
        call_nextArray(needle,next);

        int loop_h = 0,loop_n=-1;
        while ((loop_n>-1||loop_h <= haystack.length()-needle.length())&&loop_h < haystack.length())
        {
            while(loop_n>-1&&needle.charAt(loop_n+1)!=haystack.charAt(loop_h)){
                loop_n=next[loop_n];
            }
            if(needle.charAt(loop_n+1)==haystack.charAt(loop_h))
                loop_n++;
            if(loop_n==needle.length()-1)
                return loop_h-needle.length()+1;

            loop_h++;
        }
        return -1;
    }

    private void call_nextArray(String needle,int[] next){
        next[0]=-1;
        int next_point=-1;

        for(int loop = 1; loop < needle.length(); loop++) {
            while(next_point>-1&&needle.charAt(next_point+1)!=needle.charAt(loop)){
                next_point=next[next_point];
            }
            if(needle.charAt(loop)==needle.charAt(next_point+1)){
                next_point++;
            }
            next[loop]=next_point;
        }
    }
}