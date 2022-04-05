import java.io.*;
import java.util.*;
import read.*;

public class Main {
    public static void main(String[] args)  throws IOException {
        GetDoc biased = new GetDoc();
        BM25 hehe = new BM25();
        while(true) {
            System.out.println("please input the query: ");
            InputStreamReader is = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(is);
            String querys = br.readLine();
            double startTime=System.nanoTime();   //begin time
            System.out.println("ReadTest Output:" + querys);
            hehe.calculateallBM25(querys.replaceAll("[\\pP‘’“”><+=$]", ""));
            hehe.sort_result(biased, querys);
            double endTime=System.nanoTime(); //end time
            System.out.println("runtime： "+(endTime-startTime)/1000000000+"s");
        }
    }

}
