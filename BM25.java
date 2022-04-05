import read.FileReader;
import read.RelJudgement;
import java.io.*;
import java.util.*;

public class BM25 {
    static ArrayList<ArrayList<Integer>> postlist;
    static HashMap<String, Integer> termID;
    static HashMap<String, Integer> titlequery;
    static HashMap<String, Integer> doc_term;
    static ArrayList<String> IDdocno;
    static HashMap<String, Double> result_not_sort;
    static Results Result0;
    static RelJudgement judgement0;
    static String Tokens;
    static String query_id;
    OutputStreamWriter writer;
    static double average_doc_length;
    static ArrayList<Double> doc_length;
    private static final double K1 = 1.2;
    private static final double K2 = 7;
    private static final double B = 0.75;
    private static final int DOC_COUNT = 131896;

    public static void main(String[] args)  throws IOException {
        GetDoc biased = new GetDoc();
        BM25 hehe = new BM25();
        while(true) {
            System.out.println("please input the query: ");
            InputStreamReader is = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(is);
            String querys = br.readLine();
            System.out.println("ReadTest Output:" + querys);
            hehe.calculateallBM25(querys.replaceAll("[\\pP‘’“”><+=$]", ""));
            hehe.sort_result(biased, querys);
        }
    }


    public BM25() throws IOException {
        File f = new File("hw4-bm25-baseline-j2534li.txt"); //build a new file to output the result of baseline
        FileOutputStream meta_id0 = new FileOutputStream(f);//output metadata
        writer = new OutputStreamWriter(meta_id0, "UTF-8");
        result_not_sort = new HashMap<>();
        Result0 = new Results();
        FileReader filereader = new FileReader("latime");
        doc_length = filereader.readDOCIDtoLength();
        postlist = filereader.readPostList();
        termID = filereader.readTERMtoID();//constant structure for internal ID and DOCNO
        average_doc_length = Tokenize.average_doc_length(doc_length);
        IDdocno = filereader.readIDtoDOCNO();
    }

    public void sort_result(GetDoc biased, String Query) throws IOException{//sort the rank of result
        List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String,Double>>(result_not_sort.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String,Double>>() {
            //升序排序
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        int rank = 1;
        for(Map.Entry<String, Double> mapping: list) {
            System.out.print(rank+".");
            biased.getScore(mapping.getKey(), Query);
            System.out.println("");
            //System.out.println(mapping.getKey() + " " + rank + " " + mapping.getValue() + " j2534li");
            rank++;
            if(rank>10)
                break;
        }
    }

    public void calculateallBM25(String Query1) throws IOException {//select all possible document which  score above 0
        try {
            result_not_sort = new HashMap<>();
            titlequery = Tokenize.query_separate(Query1);
            for (String query : titlequery.keySet()) {
                //System.out.println(postlist.get(termID.get(query)));
                for (int i = 0; i < postlist.get(termID.get(query)).size(); i = i + 2) {//select each term's related document
                    int docid = postlist.get(termID.get(query)).get(i) - 1;
                    calculateBM25(IDdocno.get(docid), docid);
                }
            }
        }catch (NullPointerException e) {
            return;
        }
    }

    public void calculateBM25(String DOCNO, int docid) throws IOException{//calculate BM25 value in one specified documennt
        //double doclength = docterm.size();
        double score = 0;
        int num = 1;
        for(String query : titlequery.keySet()){
                score += calculateDocWeight(query, docid)*calculateQueryWeight(query)*calculateTermWeight(query);
            if(score > 0.0 && num == titlequery.keySet().size()) {
                result_not_sort.put(IDdocno.get(docid), score);
            }
            num++;
        }
        //System.out.println(IDdocno.get(docid) + " " + score);

    }

    private double calculateDocWeight(String query, int docid) throws IOException {
        double qf;
        try{
            ArrayList<Integer> postlistminor = postlist.get(termID.get(query));
            int indexqf = 0;
            ////////////////// 2 divided find//////////////
            int left = 0;
            int right = postlistminor.size() - 2;
            //System.out.println(query+ "  " + right);
            int halfmiddle;
            while(true) {
                halfmiddle = (right + left)/4;
                int middleid = postlistminor.get(2*halfmiddle);
                //System.out.println("middle: " + middle);
                //System.out.println("middleid: " + middleid);
                //System.out.println("docid: " +docid);
                //System.out.println("left: " + left);
                //System.out.println("right: " +right);
                if( middleid == docid + 1) {
                    indexqf = 2*halfmiddle+1;
                    break;}
                else if(left + 2== right) {
                    int rightid = postlistminor.get(right);
                    if(rightid == docid + 1) {
                        indexqf = right + 1;
                        break;}
                    indexqf = 0;
                    break;}
                else if(middleid < docid + 1)
                    left = 2*halfmiddle;
                else if(middleid > docid + 1)
                    right = 2*halfmiddle;
                }

            ////int indexqf = postlistminor.indexOf(docid + 1) + 1;
            //System.out.println(postlistminor);
            if(indexqf == 0)
                qf = 0;
            else
                qf = postlistminor.get(indexqf);
            //System.out.println(query + " " + qf);
        } catch (NullPointerException e) {
            qf = 0;
        }
        double doclength = doc_length.get(docid);
        double K = K1*((1 - B) + B*doclength/average_doc_length);
        double weight = (K1 + 1)*qf/(K + qf);
        return weight;
    }

    private double calculateQueryWeight(String query) throws IOException {
        double qf = titlequery.get(query);
        double weight = (K2 + 1)*qf/(K2 + qf);
        return weight;
    }

    private double calculateTermWeight(String query) throws IOException {
        int termid;
        try{
            termid = termID.get(query);
        } catch (NullPointerException e) {
            System.out.println("no such word in he term here");
            return 0;
        }
        double termdoc = postlist.get(termid).size();
        double weight = Math.log((DOC_COUNT - termdoc + 0.5)/(termdoc + 0.5));
        return weight;
    }
}