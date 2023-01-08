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

        int sch_idx = 0,next_idx=-1;
        while ((next_idx>-1||sch_idx <= haystack.length()-needle.length())&&sch_idx < haystack.length())
        {
            while(next_idx>-1&&needle.charAt(next_idx+1)!=haystack.charAt(sch_idx)){
                next_idx=next[next_idx];
            }
            if(needle.charAt(next_idx+1)==haystack.charAt(sch_idx))
                next_idx++;
            if(next_idx==needle.length()-1)
                return sch_idx-needle.length()+1;

            sch_idx++;
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